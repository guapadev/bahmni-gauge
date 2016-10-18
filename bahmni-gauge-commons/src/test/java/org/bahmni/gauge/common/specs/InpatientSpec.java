package org.bahmni.gauge.common.specs;

import com.thoughtworks.gauge.BeforeClassSteps;
import com.thoughtworks.gauge.Step;
import com.thoughtworks.gauge.Table;
import org.bahmni.gauge.common.BahmniPage;
import org.bahmni.gauge.common.DriverFactory;
import org.bahmni.gauge.common.PageFactory;
import org.bahmni.gauge.common.clinical.DispositionPage;
import org.bahmni.gauge.common.inpatient.BedAssignmentPage;
import org.bahmni.gauge.common.inpatient.InpatientDashboard;
import org.bahmni.gauge.common.inpatient.InpatientHeader;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class InpatientSpec extends BaseSpec{
    private final WebDriver driver;

    public InpatientSpec() {
        this.driver = DriverFactory.getDriver();
    }

    @BeforeClassSteps
    public void waitForAppReady() {
        BahmniPage.waitForSpinner(driver);
    }

    @Step("Select <movement> from Patient Movement and click <Action> button")
    public void movePatient(String movement,String action){
        DispositionPage disposition = PageFactory.get(DispositionPage.class);
        disposition.captureDataForDisposition(movement);
        InpatientDashboard dashboardPage = PageFactory.get(InpatientDashboard.class);
        WebElement actionElement = dashboardPage.findButtonByText(action);
        actionElement.click();
    }

    @Step("Assign an empty bed")
    public void assignBed(){
        BedAssignmentPage bedAssignmentPage = PageFactory.get(BedAssignmentPage.class);
        try {
            bedAssignmentPage.assignAnEmptyBed();
        } catch (Exception e){
            Assert.assertNull(e.getMessage(),e);
        }
    }

    @Step("Navigate to Inpatient Dashboard")
    public void gotoInpatientDashboard(){
        InpatientHeader inpatientHeader = PageFactory.get(InpatientHeader.class);
        inpatientHeader.gotoIpdDashboard();
        waitForAppReady();
    }

    @Step("Ensure inpatient icon exists on Patient Profile display control")
    public void ensureAdmitted(){
        InpatientDashboard dashboardPage = PageFactory.get(InpatientDashboard.class);
        Assert.assertTrue("inpatient icon doesn't exist", dashboardPage.isAdmitted());
    }

    @Step("Verify display control <displayControlId> on inpatient dashboard, has the following details <table>")
    public void verifyDisplayControlContent(String displayControlId, Table table) {
        InpatientDashboard dashboardPage = PageFactory.get(InpatientDashboard.class);
        String displayControlText = dashboardPage.getDisplayControlText(displayControlId);
        for (String drugOrder : table.getColumnValues("details")) {
            drugOrder = setDateTime(drugOrder);
            Assert.assertTrue(stringDoesNotExist(drugOrder),displayControlText.contains(drugOrder));
        }
    }

    @Step("Ensure inpatient icon does not exist on Patient Profile display control")
    public void ensureNotAdmitted(){
        InpatientDashboard dashboardPage = PageFactory.get(InpatientDashboard.class);
        Assert.assertFalse("inpatient icon exists", dashboardPage.isAdmitted());
    }
}