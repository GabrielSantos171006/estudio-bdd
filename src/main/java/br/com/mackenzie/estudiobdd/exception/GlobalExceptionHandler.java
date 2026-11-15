package br.com.mackenzie.estudiobdd.exception;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Tratamento de erros padronizado com ProblemDetail (RFC 9457).
 * Todas as respostas de erro têm: type, title, status, detail, instance, timestamp e "erros" (lista).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ProblemDetail problema(HttpStatus status, String titulo, String detalhe, List<String> erros) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detalhe);
        pd.setTitle(titulo);
        pd.setType(URI.create("about:blank"));
        pd.setProperty("timestamp", OffsetDateTime.now().toString());
        pd.setProperty("erros", erros);
        return pd;
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ProblemDetail naoEncontrado(RecursoNaoEncontradoException ex) {
        return problema(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage(), List.of(ex.getMessage()));
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ProblemDetail regraNegocio(RegraNegocioException ex) {
        return problema(HttpStatus.UNPROCESSABLE_CONTENT, "Regra de negócio violada", ex.getMessage(), List.of(ex.getMessage()));
    }

    @ExceptionHandler(ConflitoException.class)
    public ProblemDetail conflito(ConflitoException ex) {
        return problema(HttpStatus.CONFLICT, "Conflito", ex.getMessage(), List.of(ex.getMessage()));
    }

    @ExceptionHandler(AcessoNegadoException.class)
    public ProblemDetail acessoNegado(AcessoNegadoException ex) {
        return problema(HttpStatus.FORBIDDEN, "Operação não permitida", ex.getMessage(), List.of(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail validacao(MethodArgumentNotValidException ex) {
        List<String> erros = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .toList();
        return problema(HttpStatus.BAD_REQUEST, "Erro de validação dos dados", "Um ou mais campos são inválidos.", erros);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ProblemDetail cabecalhoAusente(MissingRequestHeaderException ex) {
        String msg = "Cabeçalho obrigatório ausente: " + ex.getHeaderName()
                + " (identidade simulada do usuário atual).";
        return problema(HttpStatus.BAD_REQUEST, "Requisição inválida", msg, List.of(msg));
    }

    @ExceptionHandler({ MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class, IllegalArgumentException.class })
    public ProblemDetail requisicaoInvalida(Exception ex) {
        String msg = ex instanceof HttpMessageNotReadableException
                ? "Corpo da requisição inválido ou com valor de enum desconhecido."
                : ex.getMessage();
        return problema(HttpStatus.BAD_REQUEST, "Requisição inválida", msg, List.of(msg));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail integridade(DataIntegrityViolationException ex) {
        String msg = "Operação viola uma restrição do banco de dados (unicidade ou chave estrangeira).";
        return problema(HttpStatus.CONFLICT, "Conflito de integridade", msg, List.of(msg));
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail erroInterno(Exception ex) {
        log.error("Erro inesperado", ex);
        String msg = "Ocorreu um erro inesperado: " + ex.getMessage();
        return problema(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor", msg, List.of(msg));
    }
}
