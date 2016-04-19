import com.google.common.base.Function;
import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import java.io.File;
import java.util.concurrent.TimeUnit;
import static java.util.concurrent.TimeUnit.SECONDS;

public class Selenium2Example
{
    public static WebDriver driver;
    private String origName = "IvankaList";
    private String origListUrl;
    private String newName = "AvianaList";
    private String prospectUrl;

    @Before
    public void BeforeTest()
    {
        File file = new File("C:/Users/kathuram/Desktop/Force/chromedriver.exe");
        System.setProperty("webdriver.chrome.driver", file.getAbsolutePath());
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, SECONDS);
        driver.get("https://pi.pardot.com");

        WebElement username = driver.findElement(By.name("email_address"));
        username.sendKeys("pardot.applicant@pardot.com");
        WebElement password = driver.findElement(By.name("password"));
        password.sendKeys("Applicant2012");
        WebElement loginButton = driver.findElement(By.name("commit"));
        loginButton.click();
        (new WebDriverWait(driver, 10)).until((ExpectedCondition<Boolean>) d -> d.getTitle().startsWith("Dashboard"));
    }

    @Test
    //Attempt to create another list with that same name and ensure the system correctly gives a validation failure
    public void Pass_FirsListCreatedWithRandomName()
    {
        origListUrl = CreateList(origName);
    }

    //@Test
    //Attempt to create another list with that same name and ensure the system correctly gives a validation failure
    public void Pass_ValidationFailureWhenSameListNameUsedAgain()
    {
        CreateList(origName);
        //Expect validation error
    }

    @Test
    //Ensure the system allows the creation of another list with the original name now that the original list is renamed
    public void Pass_RenameList()
    {
        driver.navigate().to(origListUrl);

        WebElement editListPage = driver.findElement((By.xpath("//a[@accesskey='e']")));
        editListPage.click();

        WebElement listName = driver.findElement((By.id("name")));
        listName.clear();
        listName.sendKeys(newName);

        WebElement saveListButton = driver.findElement((By.id("save_information")));
        saveListButton.click();
    }

    @Test
    //Ensure the system allows the creation of another list with the original name now that the original list is renamed
    public void Pass_AnotherListCreatedAfterFirstListRenamed()
    {
        origListUrl = CreateList(origName);
    }

    @Test
    //Add your new prospect to the newly created list
    //Ensure the new prospect is successfully added to the list upon save
    public void Pass_NewProspectAddedToList()
    {
        CreateProspect();

        driver.navigate().to(prospectUrl);

        WebElement editProspectPage = driver.findElement((By.xpath("//a[@accesskey='e']")));
        editProspectPage.click();

        WebElement listsIcon = driver.findElement((By.xpath("//i[contains(@id, 'toggle-inputs-lists')]")));
        listsIcon.click();

        WebElement listName = driver.findElement((By.xpath("//a[@class='chzn-single chzn-default']")));
        listName.click();

        WebElement listSearch = driver.findElement((By.xpath("//div[@class='chzn-search']/input")));
        listSearch.sendKeys(origName);
        listSearch.sendKeys(Keys.TAB);

        WebElement createProspectButton = driver.findElement((By.name("commit")));
        createProspectButton.click();
    }

    @Test
    //Send a text only email to the list (Marketing > Emails)  *Please note, email is disabled in this account so you will not actually be able to send the email.  This is okay.
    public void Pass_SendEmail() throws InterruptedException {
        driver.navigate().to("https://pi.pardot.com/email/draft/edit");

        WebElement name = driver.findElement((By.id("name")));
        name.sendKeys("Email Name");

        WebElement chooseFolder = findElement(driver, By.xpath("//span[@class='input-xlarge uneditable-input folder-name']"), 10);
        chooseFolder.click();

        Thread.sleep(1000);

        WebElement folderfilter = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@placeholder='Search for content by name']")));
        folderfilter.sendKeys("Rincy Test");

        WebElement folderName = driver.findElement(By.xpath("//span[@title='Rincy Test']"));
        folderName.click();

        WebElement chooseSelectedFolder = driver.findElement((By.id("select-asset")));
        chooseSelectedFolder.click();

        WebElement chooseCampaign = findElement(driver, By.xpath("//span[@class='input-xlarge uneditable-input object-name']"), 10);
        chooseCampaign.click();

        Thread.sleep(1000);

        WebElement campaignFilter = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@placeholder='Search for content by name']")));
        campaignFilter.sendKeys("Rincy Test Campaign");

        WebElement campaignName = driver.findElement(By.xpath("//h4[contains(text(), 'Rincy Test Campaign')]"));
        campaignName.click();

        WebElement chooseSelectedCampaign = driver.findElement((By.id("select-asset")));
        chooseSelectedCampaign.click();

        WebElement fromTemplate = driver.findElement((By.id("from_template")));
        if (fromTemplate.isSelected()) {
            fromTemplate.click();
        }

        WebElement saveEmail = driver.findElement((By.id("save_information")));
        saveEmail.click();

        Thread.sleep(3000);

        WebElement sendingTab = driver.findElement((By.id("flow_sending")));
        sendingTab.click();

        Thread.sleep(2000);

        WebElement listName = driver.findElement((By.xpath("//a[@class='chzn-single chzn-default']")));
        listName.click();

        WebElement listSearch = driver.findElement((By.xpath("//div[@class='chzn-search']/input")));
        listSearch.sendKeys(origName);
        listSearch.sendKeys(Keys.TAB);

        Select sender = new Select(driver.findElement(By.name("a_sender[]")));
        sender.selectByVisibleText("General User");

        WebElement generalName = driver.findElement((By.name("a_general_name")));
        generalName.sendKeys("Sender Name");

        WebElement generalEmail = driver.findElement((By.name("a_general_email")));
        generalEmail.sendKeys("sender.email@outlook.com");

        WebElement subjectLine = driver.findElement((By.name("subject_a")));
        subjectLine.sendKeys("Test email");

        WebElement saveFooter = driver.findElement((By.id("save_footer")));
        saveFooter.click();
    }

    @After
    public void AfterTest() {
        WebElement acctToggle = driver.findElement((By.id("acct-tog")));
        acctToggle.click();

        WebElement logoutButton = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@href='/user/logout']")));
        logoutButton.click();

        driver.quit();
    }

    private String CreateList(String desiredListName)
    {
        driver.navigate().to("https://pi.pardot.com/list");

        WebElement addList = findElement(driver, By.id("listxistx_link_create"), 10);
        addList.click();

        WebElement listName = findElement(driver, By.name("name"), 10);
        listName.sendKeys(desiredListName);

        WebElement chooseButton = findElement(driver, By.xpath("//button[@class='btn choose-asset']"), 10);
        chooseButton.click();

        driver.manage().timeouts().implicitlyWait(10, SECONDS);

        WebElement filter = findElement(driver, By.xpath("//input[@placeholder='Search for content by name']"), 10);
        filter.sendKeys("Rincy Test");

        WebElement folderName = driver.findElement(By.xpath("//span[@title='Rincy Test']"));
        folderName.click();

        WebElement chooseSelectedButton = driver.findElement((By.id("select-asset")));
        chooseSelectedButton.click();

        WebElement createListButton = driver.findElement((By.id("save_information")));
        createListButton.click();

        return driver.getCurrentUrl();
    }

    private void CreateProspect()
    {
        driver.navigate().to("https://pi.pardot.com/prospect");

        WebElement addProspectButton = driver.findElement((By.id("pr_link_create")));
        addProspectButton.click();

        WebElement email = driver.findElement((By.id("email")));
        email.sendKeys("rincy.madaan@gmail.com");

        Select campaign = new Select(driver.findElement(By.id("campaign_id")));
        campaign.selectByVisibleText("Ben Wildcats");

        Select profile = new Select(driver.findElement(By.id("profile_id")));
        profile.selectByVisibleText("Ben Wildcats 1");

        WebElement score = driver.findElement((By.id("score")));
        score.sendKeys("100");

        WebElement createProspectButton = driver.findElement((By.name("commit")));
        createProspectButton.click();

        prospectUrl = driver.getCurrentUrl();
    }

    private static WebElement findElement(final WebDriver driver, final By locator, final int timeoutSeconds) {
        FluentWait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .withTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .pollingEvery(1, TimeUnit.SECONDS)
                .ignoring(NoSuchElementException.class);

        return wait.until(new Function<WebDriver, WebElement>() {
            public WebElement apply(WebDriver webDriver) {
                return driver.findElement(locator);
            }
        });
    }
}