package org.bahmni.gauge.common.specs;

import com.thoughtworks.gauge.BeforeClassSteps;
import com.thoughtworks.gauge.Step;
import com.thoughtworks.gauge.Table;
import com.thoughtworks.gauge.TableRow;
import org.bahmni.gauge.common.BahmniPage;
import org.bahmni.gauge.common.DriverFactory;
import org.bahmni.gauge.common.PageFactory;
import org.bahmni.gauge.common.TestSpecException;
import org.bahmni.gauge.common.clinical.DashboardPage;
import org.bahmni.gauge.common.clinical.ObservationsPage;
import org.bahmni.gauge.common.clinical.domain.DrugOrder;
import org.bahmni.gauge.common.clinical.domain.ObservationForm;
import org.bahmni.gauge.common.program.domain.PatientProgram;
import org.bahmni.gauge.common.registration.domain.Patient;
import org.bahmni.gauge.rest.BahmniRestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dharmens on 9/5/16.
 */
public class ObservationSpec extends BahmniPage {

    public ObservationSpec() {
        driver = DriverFactory.getDriver();
    }

    @BeforeClassSteps
    public void waitForAppReady() {
        new BahmniPage().waitForSpinner(DriverFactory.getDriver());
    }


    @Step("Select the template <template> from on the observation page")
    public void clickOnTreatmentEnrollment(String template) throws InterruptedException {
        ObservationsPage observationsPage = PageFactory.getObservationsPage();
        template = template.replace(" ", "_");
        observationsPage.selectTemplate(template);
    }

    @Step("Create a <formName> form with following data <table>")
    public void createForm(String formName, Table table) {
        Patient patient = new BahmniPage().getPatientFromSpecStore();
        PatientProgram patientProgram = new BahmniPage().getPatientProgramFromSpecStore();

        Map<String, Object> formAttributes = new HashMap<>();
        formAttributes.put("patientUuid", patient.getUuid());
        formAttributes.put("patientProgramUuid", patientProgram.getPatientProgramUuid());
        formAttributes.putAll(transformTableToMap(table));

        BahmniRestClient.get().createForm(formName + ".ftl", formAttributes);

    }

    private Map<String, String> transformTableToMap(Table table) {

        Map<String, String> formVariables = new HashMap<>();
        List<String> columnNames = table.getColumnNames();

        for (String columnName : columnNames) {
            formVariables.put(columnName, table.getTableRows().get(0).getCell(columnName));
        }

        return formVariables;
    }

    @Step("Verify observations recorded under <formName>")
    public void verifyObservationsOnDashboard(String formName) {
        DashboardPage dashboardPage = PageFactory.getDashboardPage();
        dashboardPage.validateObservationDisplayControl(formName);
    }

    @Step("Verify prescribed drugs on the dashboard page")
    public void verifyDrugsOnDashboard() {
        List<DrugOrder> drugOrder = (List<DrugOrder>) new BahmniPage().getDrugOrderFromSpecStore();
        DashboardPage dashboardPage = PageFactory.getDashboardPage();
        for (DrugOrder drug : drugOrder)
            dashboardPage.validateDrugOrderDisplayControl(drug, "All active TB Drugs");
    }


    @Step("Close the app")
    public void closeApplication() {
        new BahmniPage().closeApp(driver);
    }
}