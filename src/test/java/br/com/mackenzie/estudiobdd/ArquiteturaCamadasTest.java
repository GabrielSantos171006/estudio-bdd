package br.com.mackenzie.estudiobdd;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(
    packages = "br.com.mackenzie.estudiobdd",
    importOptions = ImportOption.DoNotIncludeTests.class
)
public class ArquiteturaCamadasTest {

    @ArchTest
    static final ArchRule controllers_nao_devem_acessar_repositories =
        noClasses()
            .that().resideInAPackage("..controller..")
            .should().dependOnClassesThat().resideInAPackage("..repository..")
            .because("A camada Controller deve conversar exclusivamente com DTOs e Services, nunca direto com Repositories.");

    @ArchTest
    static final ArchRule services_nao_devem_depender_de_controllers =
        noClasses()
            .that().resideInAPackage("..service..")
            .should().dependOnClassesThat().resideInAPackage("..controller..")
            .because("A dependência entre camadas deve ser estritamente unidirecional (Controller -> Service).");
}