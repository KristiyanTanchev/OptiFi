package com.optifi;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArchitectureTest {

    private final JavaClasses classes =
            new ClassFileImporter()
                    .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                    .importPackages("com.optifi");

    @Test
    void api_should_not_depend_on_repository() {
        noClasses()
                .that().resideInAPackage("..api..")
                .should().dependOnClassesThat()
                .resideInAPackage("..repository..")
                .check(classes);
    }

    @Test
    void api_should_not_depend_on_model() {
        noClasses()
                .that().resideInAPackage("..api..")
                .should().dependOnClassesThat()
                .resideInAPackage("..model..")
                .check(classes);
    }

    @Test
    void application_should_not_depend_on_api() {
        noClasses()
                .that().resideInAPackage("..application..")
                .should().dependOnClassesThat()
                .resideInAPackage("..api..")
                .check(classes);
    }

    @Test
    void repository_should_not_depend_on_application() {
        noClasses()
                .that().resideInAPackage("..repository..")
                .should().dependOnClassesThat()
                .resideInAPackage("..application..")
                .check(classes);
    }

    @Test
    void repository_should_not_depend_on_api() {
        noClasses()
                .that().resideInAPackage("..repository..")
                .should().dependOnClassesThat()
                .resideInAPackage("..api..")
                .check(classes);
    }

    @Test
    void model_should_not_depend_on_api() {
        noClasses()
                .that().resideInAPackage("..model..")
                .should().dependOnClassesThat()
                .resideInAPackage("..api..")
                .check(classes);
    }

    @Test
    void model_should_not_depend_on_application() {
        noClasses()
                .that().resideInAPackage("..model..")
                .should().dependOnClassesThat()
                .resideInAPackage("..application..")
                .check(classes);
    }

    @Test
    void model_should_not_depend_on_repository() {
        noClasses()
                .that().resideInAPackage("..model..")
                .should().dependOnClassesThat()
                .resideInAPackage("..repository..")
                .check(classes);
    }
}
