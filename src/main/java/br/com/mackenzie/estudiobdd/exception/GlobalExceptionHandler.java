package br.com.mackenzie.estudiobdd.exception;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.mackenzie.estudiobdd.dto.ErroRespostaDTO;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Captura erros de regras de negócio (lançados pelos Services com IllegalArgumentException)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroRespostaDTO> tratarRegraNegocio(IllegalArgumentException ex) {
        ErroRespostaDTO erro = new ErroRespostaDTO(
            HttpStatus.BAD_REQUEST.value(),
            "Regra de Negócio Violada",
            List.of(ex.getMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    // 2. Captura erros do Bean Validation (@NotBlank, @NotNull, etc., disparados nos DTOs de entrada)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroRespostaDTO> tratarValidacoesDTO(MethodArgumentNotValidException ex) {
        List<String> errosDetectados = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(f -> f.getField() + ": " + f.getDefaultMessage())
            .toList();

        ErroRespostaDTO erro = new ErroRespostaDTO(
            HttpStatus.BAD_REQUEST.value(),
            "Erro de Validação de Dados",
            errosDetectados
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    // 3. Captura qualquer outro erro inesperado do servidor
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroRespostaDTO> tratarErrosGerais(Exception ex) {
        ErroRespostaDTO erro = new ErroRespostaDTO(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Erro Interno do Servidor",
            List.of("Ocorreu um erro inesperado: " + ex.getMessage())
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }
}