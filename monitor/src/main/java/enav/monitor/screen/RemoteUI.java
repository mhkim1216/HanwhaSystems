/**
 * Created 01.02.2018.
 * Last Modified 02.08.2018.
 * Layout for overview screen has been built using JavaFX.
 * 
 * 
 */

package enav.monitor.screen;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import enav.monitor.polling.PollingManager;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.Gauge.SkinType;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.Section;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class RemoteUI extends Application
{
	private Stage primaryStage;
	private TabPane tPane;
	private BorderPane bPane;
	private VBox rPane;
	private StackPane tvPane;
	private HBox verPane;
	private Scene scene;
	private Thread timeThread;
	private String rootColor = "#1b221b";
	private double ramTotal;
	private double ramUsed;
	private double cpuTotal;
	private double cpuUsed;
	private double diskTotal;
	private double diskUsed;
	private double netTotal;
	private double netUsed;
	private double efficiency;
	private PollingManager manager;
	private Pane hPane;
	private Pane logPane;
	private Pane errPane;
	private History history;
	private LogTrace logTrace;
	private ErrorTrace errorTrace;
	private Gauge ramGauge;
	private Gauge cpuGauge;
	private Gauge diskGauge;
	private Gauge netGauge;
	private Gauge summaryGauge;
	private String ip;
	private final static int port = 1216;
	private double stageX;
	private double stageY;
	private double mouseX; // previous X position
	private double mouseY; // previous Y position
	private Label serverInfo;
	private Label opStatusLabel;

	private Label tomcatLB;
	private Label postgresLB;
	private Label springLB;
	private Label restapiLB;
	private Label javaLB;
	private Label javaClientLB;
	private Label dllClientLB;

	private TextField reqText;
	private TextField reqTypeText;
	private TextField SVModuleText;
	private TextField paramText;
	private TextField sIdText;
	private TextField sTimeText;
	private TextField tempSlotText;
	private TextArea reqQueryText;

	private TextField errTypeText;
	private TextField errNameText;
	private TextField errTimeText;

	private TextArea allLog;
	private StringBuilder sb;

	private String status = "Not Connected";

	public RemoteUI()
	{
		efficiency = 0;
		sb = new StringBuilder();
	}

	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void init() throws Exception
	{
		// TODO Auto-generated method stub
		super.init();
	}

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		setBorderPane();
		setTabPane();
		buildVersionPane();
		setRootPane();
		setScene();
		setStage(primaryStage);

		manager = PollingManager.getInstance(this, history, logTrace, errorTrace);
		// add codes for running manager's thread in background
		// manager.monitor("192.168.0.184", port);
		manager.monitor("127.0.0.1", port);
	}

	@Override
	public void stop() throws Exception
	{
		// TODO Auto-generated method stub
		super.stop();
	}

	private void setBorderPane()
	{
		/* Setting 5 Regions of BorderPane */
		buildLeftPane();
		buildTopPane();
		buildCenterPane();
		buildRightPane();
		buildBottomPane();
	}

	private void setScene()
	{
		scene = new Scene(rPane, 1280, 785);
		// scene.setFill(Color.rgb(0, 33, 63, 1));
	}

	private void setStage(Stage stage)
	{
		primaryStage = stage;
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.setTitle("eNavigation DSP Monitor");
		primaryStage.setScene(scene);
		primaryStage.setAlwaysOnTop(true);
		stageX = primaryStage.getX();
		stageY = primaryStage.getY();
		primaryStage.show();
	}

	private void setTabPane()
	{
		tvPane = new StackPane();

		// create another tabs
		hPane = getHistory();
		logPane = getLogTrace();
		errPane = getErrorTrace();

		try
		{
			/* Applied FXML & CSS */
			tPane = FXMLLoader.load(getClass().getResource("/fxml/TabPane.fxml"));
			tPane.getStylesheets().add("/css/TabPane.css");
			tPane.setPadding(new Insets(10, 20, 20, 20));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (NullPointerException e)
		{
			tPane = new TabPane();
			e.printStackTrace();
		}
		List<Tab> tabs = new ArrayList<Tab>();
		String[] tabLabel = new String[] { "Overview", "History", "Log Trace", "Error Trace" };
		// Assign Layout Pane
		Pane[] tabContent = new Pane[] { bPane, hPane, logPane, errPane };
		DropShadow shadow = new DropShadow();
		shadow.setColor(Color.BLACK);
		shadow.setOffsetX(4.0);
		shadow.setOffsetY(4.0);
		shadow.setRadius(10);

		for (int i = 0; i < tabLabel.length; ++i)
		{
			Tab tab = new Tab(tabLabel[i]);
			tab.setClosable(false);
			StackPane sPane = new StackPane();
			sPane.setPadding(new Insets(0, 20, 20, 5));
			sPane.getChildren().add(tabContent[i]);
			tab.setContent(sPane);
			tabContent[i].setStyle("-fx-background-color: #111412;-fx-border-color:transparent;");
			tabContent[i].setEffect(shadow);
			tabs.add(tab);
		}
		tPane.getTabs().addAll(tabs);
		tvPane.getChildren().add(tPane);
	}

	private void setRootPane()
	{
		rPane = new VBox();
		rPane.setStyle("-fx-background-color: " + rootColor + ";");
		MenuBar menuBar = new MenuBar();
		menuBar.setStyle("-fx-background-color: " + rootColor + ";");
		menuBar.setPadding(new Insets(0, 0, 0, 20));
		ArrayList<Menu> menus = new ArrayList<Menu>();
		String[] menuList = new String[] { "File", "Home", "Edit", "Search", "Setting", "Window", "Help" };
		Menu menu;
		Label label;
		for (int i = 0; i < menuList.length; ++i)
		{
			menu = new Menu();
			label = new Label(menuList[i]);
			label.setTextFill(Color.WHITE);
			menu.setGraphic(label);
			menus.add(menu);
		}
		menuBar.getMenus().addAll(menus);
		Label titleLabel = new Label("Remote Diagnostic System 0.1 ");

		titleLabel.setOnMousePressed(new EventHandler<MouseEvent>()
		{
			public void handle(MouseEvent e)
			{
				mouseX = e.getX();
				mouseY = e.getY();
			}
		});

		titleLabel.setOnMouseReleased(new EventHandler<MouseEvent>()
		{
			public void handle(MouseEvent e)
			{
				primaryStage.setX(primaryStage.getX() + (e.getX() - mouseX));
				primaryStage.setY(primaryStage.getY() + (e.getY() - mouseY));
			}
		});

		titleLabel.setStyle("-fx-text-fill: white;");
		titleLabel.setFont(new Font(18));
		titleLabel.setPadding(new Insets(10));
		FlowPane titleBar = new FlowPane();
		FileInputStream fis = null;
		Image titleImage;
		ImageView imageView = null;
		try
		{
			fis = new FileInputStream("src/main/resources/images/hanwha.png");
			titleImage = new Image(fis);
			imageView = new ImageView(titleImage);
			imageView.setFitHeight(100);
			imageView.setFitWidth(100);
			imageView.setPreserveRatio(true);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (NullPointerException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (fis != null)
				try
				{
					fis.close();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		Button exitButton = new Button("X");
		InnerShadow innerShadow = new InnerShadow(13, Color.BLACK);
		exitButton.setStyle("-fx-background-radius: 5em; " + "-fx-background-color: #FF0000;");
		exitButton.setFont(Font.font("", FontWeight.BOLD, 9));
		exitButton.setTextFill(Color.BLACK);
		exitButton.setEffect(innerShadow);
		exitButton.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent event)
			{
				timeThread.interrupt();
				primaryStage.close();
				System.exit(0);
			}
		});
		Button minButton = new Button("-");
		minButton.setStyle("-fx-background-radius: 5em; " + "-fx-background-color: #6ED746;");
		minButton.setFont(Font.font("", FontWeight.BOLD, 9));
		minButton.setTextFill(Color.BLACK);
		minButton.setEffect(innerShadow);
		minButton.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent event)
			{
				primaryStage.setIconified(true);
			}
		});
		AnchorPane titleBarButtons = new AnchorPane();
		AnchorPane.setTopAnchor(exitButton, 13.0);
		AnchorPane.setLeftAnchor(exitButton, 840.0);
		AnchorPane.setTopAnchor(minButton, 13.0);
		AnchorPane.setLeftAnchor(minButton, 816.0);
		titleBarButtons.getChildren().add(exitButton);
		titleBarButtons.getChildren().add(minButton);
		titleBar.setPadding(new Insets(2, 10, 2, 10));
		titleBar.setAlignment(Pos.CENTER_LEFT);
		titleBar.getChildren().add(titleLabel);
		titleBar.getChildren().add(imageView);
		titleBar.getChildren().add(titleBarButtons);

		rPane.getChildren().addAll(titleBar, menuBar, tvPane);
	}

	private void buildVersionPane()
	{
		verPane = new HBox(10);

		FileInputStream fis = null;

		Image tomcatImage;
		Image postgresImage;
		Image springImage;
		Image restapiImage;
		Image javaImage;
		Image javaClientImage;
		Image dllClientImage;
		ImageView tomcatIV = null;
		ImageView postgresIV = null;
		ImageView springIV = null;
		ImageView restapiIV = null;
		ImageView javaIV = null;
		ImageView javaClientIV = null;
		ImageView dllClientIV = null;

		try
		{
			fis = new FileInputStream("src/main/resources/images/tomcat.png");
			tomcatImage = new Image(fis);
			fis.close();
			tomcatIV = new ImageView(tomcatImage);
			tomcatIV.setFitHeight(20);
			tomcatIV.setFitWidth(100);
			tomcatIV.setPreserveRatio(true);

			fis = new FileInputStream("src/main/resources/images/postgres.png");
			postgresImage = new Image(fis);
			fis.close();
			postgresIV = new ImageView(postgresImage);
			postgresIV.setFitHeight(20);
			postgresIV.setFitWidth(100);
			postgresIV.setPreserveRatio(true);

			fis = new FileInputStream("src/main/resources/images/spring.png");
			springImage = new Image(fis);
			fis.close();
			springIV = new ImageView(springImage);
			springIV.setFitHeight(20);
			springIV.setFitWidth(100);
			springIV.setPreserveRatio(true);

			fis = new FileInputStream("src/main/resources/images/restapi.png");
			restapiImage = new Image(fis);
			fis.close();
			restapiIV = new ImageView(restapiImage);
			restapiIV.setFitHeight(20);
			restapiIV.setFitWidth(100);
			restapiIV.setPreserveRatio(true);
			restapiIV.setTranslateY(2);

			fis = new FileInputStream("src/main/resources/images/java.png");
			javaImage = new Image(fis);
			fis.close();
			javaIV = new ImageView(javaImage);
			javaIV.setFitHeight(20);
			javaIV.setFitWidth(200);
			javaIV.setPreserveRatio(true);
			javaIV.setTranslateY(1);

			// fis = new FileInputStream("src/main/resources/images/javaClient.png");
			// javaClientImage = new Image(fis);
			// fis.close();
			// javaClientIV = new ImageView(javaClientImage);
			// javaClientIV.setFitHeight(50);
			// javaClientIV.setFitWidth(200);
			// javaClientIV.setPreserveRatio(true);
			//
			// fis = new FileInputStream("src/main/resources/images/dllClient.png");
			// dllClientImage = new Image(fis);
			// fis.close();
			// dllClientIV = new ImageView(dllClientImage);
			// dllClientIV.setFitHeight(50);
			// dllClientIV.setFitWidth(200);
			// dllClientIV.setPreserveRatio(true);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (NullPointerException e)
		{
			e.printStackTrace();
		}

		tomcatLB = new Label();
		tomcatLB.setPrefWidth(50);
//		tomcatLB.setAlignment(Pos.CENTER);
		tomcatLB.setTranslateY(2);
		tomcatLB.setTextFill(Color.BURLYWOOD);
		tomcatLB.setFont(Font.font(13));
		postgresLB = new Label();
		postgresLB.setPrefWidth(50);
//		postgresLB.setAlignment(Pos.CENTER);
		postgresLB.setTranslateY(2);
		postgresLB.setTextFill(Color.BURLYWOOD);
		postgresLB.setFont(Font.font(13));
		springLB = new Label();
		springLB.setPrefWidth(50);
//		springLB.setAlignment(Pos.CENTER);
		springLB.setTranslateY(2);
		springLB.setTextFill(Color.BURLYWOOD);
		springLB.setFont(Font.font(13));
		restapiLB = new Label();
		restapiLB.setPrefWidth(50);
//		restapiLB.setAlignment(Pos.CENTER);
		restapiLB.setTranslateY(2);
		restapiLB.setTextFill(Color.BURLYWOOD);
		restapiLB.setFont(Font.font(13));
		javaLB = new Label();
		javaLB.setPrefWidth(50);
//		javaLB.setAlignment(Pos.CENTER);
		javaLB.setTranslateY(2);
		javaLB.setTextFill(Color.BURLYWOOD);
		javaLB.setFont(Font.font(13));
		// javaClientLB = new Label("checking");
		// javaClientLB.setPrefWidth(50);
		// javaClientLB.setAlignment(Pos.CENTER);
		// javaClientLB.setTranslateY(2);
		// javaClientLB.setTextFill(Color.BURLYWOOD);
		// javaClientLB.setFont(Font.font(13));
		// dllClientLB = new Label("checking");
		// dllClientLB.setPrefWidth(50);
		// dllClientLB.setAlignment(Pos.CENTER);
		// dllClientLB.setTranslateY(2);
		// dllClientLB.setTextFill(Color.BURLYWOOD);
		// dllClientLB.setFont(Font.font(13));

		verPane.getChildren().addAll(tomcatIV, tomcatLB, postgresIV, postgresLB, springIV, springLB, restapiIV,
				restapiLB, javaIV, javaLB);
		verPane.setTranslateX(505);
		verPane.setTranslateY(-323);
		verPane.setMaxHeight(50);

		tvPane.getChildren().add(verPane);
	}

	private void buildLeftPane()
	{
		bPane = new BorderPane();
		bPane.setMinWidth(1225);
		bPane.setMinHeight(645);
		bPane.setPadding(new Insets(5, 10, 0, 10));

		VBox leftPane = new VBox();
		leftPane.setPadding(new Insets(10, 5, 0, 20));

		serverInfo = new Label("[Server Info] - " + status);
		serverInfo.setTextFill(Color.BURLYWOOD);
		serverInfo.setFont(new Font(15));
		serverInfo.setPadding(new Insets(0, 0, -23, 0));

		summaryGauge = GaugeBuilder.create().skinType(SkinType.SIMPLE).decimals(2).maxValue(100).unit("%")
				.title("USAGE").maxWidth(180).foregroundBaseColor(Color.BURLYWOOD).build();
		summaryGauge.setNeedleColor(Color.DARKGOLDENROD);
		summaryGauge.setValue(efficiency * 100);
		summaryGauge.setBarEffectEnabled(true);
		summaryGauge.setPadding(new Insets(10, -20, -15, 32));

		summaryGauge.setNeedleColor(Color.rgb(16, 55, 19));
		summaryGauge.addSection(new Section(0, 10, Color.rgb(27, 94, 32)));
		summaryGauge.addSection(new Section(10, 20, Color.rgb(46, 125, 50)));
		summaryGauge.addSection(new Section(20, 30, Color.rgb(56, 142, 60)));
		summaryGauge.addSection(new Section(30, 40, Color.rgb(67, 160, 71)));
		summaryGauge.addSection(new Section(40, 50, Color.rgb(76, 175, 80)));
		summaryGauge.addSection(new Section(50, 60, Color.rgb(102, 187, 106)));
		summaryGauge.addSection(new Section(60, 70, Color.rgb(129, 199, 132)));
		summaryGauge.addSection(new Section(70, 80, Color.rgb(165, 214, 167)));
		summaryGauge.addSection(new Section(80, 90, Color.rgb(200, 230, 201)));
		summaryGauge.addSection(new Section(90, 100, Color.rgb(232, 245, 233)));

		HBox opTitle = new HBox();
		opTitle.setPadding(new Insets(-3, 0, 11, 0));

		opStatusLabel = new Label("IDLE");
		opStatusLabel.setTextFill(Color.rgb(255, 255, 0));
		opStatusLabel.setFont(new Font(15));

		Label opInfo = new Label("[Operation] - ");
		opInfo.setTextFill(Color.BURLYWOOD);
		opInfo.setFont(new Font(15));

		opTitle.getChildren().addAll(opInfo, opStatusLabel);

		HBox errType = new HBox();
		HBox errName = new HBox();
		HBox errTime = new HBox();

		errType.setPadding(new Insets(0, 0, 10, 0));
		errName.setPadding(new Insets(0, 0, 10, 0));
		errTime.setPadding(new Insets(0, 0, 10, 0));

		Label errTypeLabel = new Label("· Err. Type");
		errTypeLabel.setTextFill(Color.WHITE);
		errTypeLabel.setMinWidth(Region.USE_COMPUTED_SIZE);
		errTypeLabel.setPrefWidth(100);
		errTypeLabel.setFont(new Font(14));
		Label errNameLabel = new Label("· Err. Name");
		errNameLabel.setTextFill(Color.WHITE);
		errNameLabel.setMinWidth(Region.USE_COMPUTED_SIZE);
		errNameLabel.setPrefWidth(100);
		errNameLabel.setFont(new Font(14));
		Label errTimeLabel = new Label("· Err. Time");
		errTimeLabel.setTextFill(Color.WHITE);
		errTimeLabel.setMinWidth(Region.USE_PREF_SIZE);
		errTimeLabel.setPrefWidth(100);
		errTimeLabel.setFont(new Font(14));

		errTypeText = new TextField();
		errTypeText.getStylesheets().add("/css/TextField.css");
		errTypeText.setPrefColumnCount(12);
		errTypeText.setEditable(false);
		errTypeText.setAlignment(Pos.CENTER);
		errNameText = new TextField();
		errNameText.getStylesheets().add("/css/TextField.css");
		errNameText.setPrefColumnCount(12);
		errNameText.setEditable(false);
		errNameText.setAlignment(Pos.CENTER);
		errTimeText = new TextField();
		errTimeText.getStylesheets().add("/css/TextField.css");
		errTimeText.setPrefColumnCount(12);
		errTimeText.setEditable(false);
		errTimeText.setAlignment(Pos.CENTER);

		errType.getChildren().addAll(errTypeLabel, errTypeText);
		errName.getChildren().addAll(errNameLabel, errNameText);
		errTime.getChildren().addAll(errTimeLabel, errTimeText);

		leftPane.getChildren().addAll(serverInfo, summaryGauge, opTitle, errType, errName, errTime);
		bPane.setLeft(leftPane);
	}

	private void buildTopPane()
	{

	}

	private void buildCenterPane()
	{
		VBox cPane = new VBox();
		cPane.setPadding(new Insets(10, 20, 10, 25));

		Label reqInfoLabel = new Label("[Request Information]");

		HBox requestor = new HBox();
		HBox reqType = new HBox();
		HBox SVModule = new HBox();
		HBox reqParam = new HBox();
		HBox sessionID = new HBox();
		HBox sessionTime = new HBox();
		HBox tempSlot = new HBox();
		HBox reqQuery = new HBox();

		reqInfoLabel.setPadding(new Insets(0, 0, 8, 0));
		requestor.setPadding(new Insets(0, 0, 8, 0));
		SVModule.setPadding(new Insets(0, 0, 8, 0));
		reqParam.setPadding(new Insets(0, 0, 8, 0));
		reqType.setPadding(new Insets(0, 0, 8, 0));
		sessionID.setPadding(new Insets(0, 0, 8, 0));
		sessionTime.setPadding(new Insets(0, 0, 8, 0));
		tempSlot.setPadding(new Insets(0, 0, 8, 0));
		reqQuery.setPadding(new Insets(0, 0, 13, 0));

		reqInfoLabel.setTextFill(Color.BURLYWOOD);
		reqInfoLabel.setFont(new Font(15));

		Label reqLabel = new Label("· Requestor");
		reqLabel.setTextFill(Color.WHITE);
		reqLabel.setMinWidth(Region.USE_COMPUTED_SIZE);
		reqLabel.setPrefWidth(140);
		reqLabel.setFont(new Font(14));

		Label reqTypeLabel = new Label("· Request Type");
		reqTypeLabel.setTextFill(Color.WHITE);
		reqTypeLabel.setMinWidth(Region.USE_COMPUTED_SIZE);
		reqTypeLabel.setPrefWidth(140);
		reqTypeLabel.setFont(new Font(14));

		Label SVModuleLabel = new Label("· Service Module");
		SVModuleLabel.setTextFill(Color.WHITE);
		SVModuleLabel.setMinWidth(Region.USE_PREF_SIZE);
		SVModuleLabel.setPrefWidth(140);
		SVModuleLabel.setFont(new Font(14));

		Label paramLabel = new Label("· Parameter");
		paramLabel.setTextFill(Color.WHITE);
		paramLabel.setMinWidth(Region.USE_PREF_SIZE);
		paramLabel.setPrefWidth(140);
		paramLabel.setFont(new Font(14));

		Label sIdLabel = new Label("· Session ID");
		sIdLabel.setTextFill(Color.WHITE);
		sIdLabel.setMinWidth(Region.USE_PREF_SIZE);
		sIdLabel.setPrefWidth(140);
		sIdLabel.setFont(new Font(14));

		Label sTimeLabel = new Label("· Session Time");
		sTimeLabel.setTextFill(Color.WHITE);
		sTimeLabel.setMinWidth(Region.USE_PREF_SIZE);
		sTimeLabel.setPrefWidth(140);
		sTimeLabel.setFont(new Font(14));

		Label tempSlotLabel = new Label("· Temporary Slot");
		tempSlotLabel.setTextFill(Color.WHITE);
		tempSlotLabel.setMinWidth(Region.USE_PREF_SIZE);
		tempSlotLabel.setPrefWidth(140);
		tempSlotLabel.setFont(new Font(14));

		Label reqQueryLabel = new Label("· Request Query (PostgreSQL)");
		reqQueryLabel.setTextFill(Color.WHITE);
		reqQueryLabel.setMinWidth(Region.USE_PREF_SIZE);
		reqQueryLabel.setPrefWidth(240);
		reqQueryLabel.setFont(new Font(14));

		reqText = new TextField();
		reqText.getStylesheets().add("/css/TextField.css");
		reqText.setPrefColumnCount(18);
		reqText.setEditable(false);
		reqText.setAlignment(Pos.CENTER);
		reqTypeText = new TextField();
		reqTypeText.getStylesheets().add("/css/TextField.css");
		reqTypeText.setPrefColumnCount(18);
		reqTypeText.setEditable(false);
		reqTypeText.setAlignment(Pos.CENTER);
		SVModuleText = new TextField();
		SVModuleText.getStylesheets().add("/css/TextField.css");
		SVModuleText.setPrefColumnCount(18);
//		SVModuleText.setEditable(false);
		SVModuleText.setAlignment(Pos.CENTER);
		paramText = new TextField();
		paramText.getStylesheets().add("/css/TextField.css");
		paramText.setPrefColumnCount(18);
		// paramText.setEditable(false);
		// paramText.setAlignment(Pos.CENTER);
		sIdText = new TextField();
		sIdText.getStylesheets().add("/css/TextField.css");
		sIdText.setPrefColumnCount(18);
		// sIdText.setEditable(false);
		// sIdText.setAlignment(Pos.CENTER);
		sTimeText = new TextField();
		sTimeText.getStylesheets().add("/css/TextField.css");
		sTimeText.setPrefColumnCount(18);
		sTimeText.setEditable(false);
		sTimeText.setAlignment(Pos.CENTER);
		tempSlotText = new TextField();
		tempSlotText.getStylesheets().add("/css/TextField.css");
		tempSlotText.setPrefColumnCount(18);
		tempSlotText.setEditable(false);
		tempSlotText.setAlignment(Pos.CENTER);
		reqQueryText = new TextArea();
		reqQueryText.getStylesheets().add("/css/TextArea.css");
		reqQueryText.setPrefRowCount(8);
		reqQueryText.setEditable(false);
		reqQueryText.setWrapText(true);

		Label[] ok = new Label[7];
		for (int i = 0; i < 7; ++i)
		{
			ok[i] = new Label("PASSED");
			ok[i].setTranslateX(20);
			ok[i].getStylesheets().add("/css/checkLabelBorder.css");
		}

		requestor.getChildren().addAll(reqLabel, reqText, ok[0]);
		reqType.getChildren().addAll(reqTypeLabel, reqTypeText, ok[1]);
		SVModule.getChildren().addAll(SVModuleLabel, SVModuleText, ok[2]);
		reqParam.getChildren().addAll(paramLabel, paramText, ok[3]);
		sessionID.getChildren().addAll(sIdLabel, sIdText, ok[4]);
		sessionTime.getChildren().addAll(sTimeLabel, sTimeText, ok[5]);
		tempSlot.getChildren().addAll(tempSlotLabel, tempSlotText, ok[6]);
		reqQuery.getChildren().add(reqQueryLabel);

		cPane.getChildren().addAll(reqInfoLabel, requestor, reqType, SVModule, reqParam, sessionID, sessionTime,
				tempSlot, reqQuery, reqQueryText);
		bPane.setCenter(cPane);
	}

	private void buildRightPane()
	{
		VBox rightPane = new VBox();
		rightPane.setPadding(new Insets(10, 20, 20, 10));

		Label logTitle = new Label("[Server Logs]");
		logTitle.setTextFill(Color.BURLYWOOD);
		logTitle.setFont(new Font(15));
		logTitle.setPadding(new Insets(0, 0, 13, 0));
		allLog = new TextArea();
		allLog.getStylesheets().add("/css/TextArea.css");
		allLog.setPrefRowCount(10);
		allLog.setWrapText(true);
		allLog.toFront();

		Label statusTitle = new Label("[Server Status]");
		statusTitle.setTextFill(Color.BURLYWOOD);
		statusTitle.setFont(new Font(15));
		statusTitle.setPadding(new Insets(15, 0, 10, 0));

		HBox SVInfoPane = new HBox(10);
		SVInfoPane.setPadding(new Insets(0, 10, 0, 0));
		// Adding RAM usage of a server

		ramGauge = GaugeBuilder.create().skinType(SkinType.SLIM).decimals(0).unit("MBYTE").title("USED").build();

		// Adding CPU usage of a server

		cpuGauge = GaugeBuilder.create().skinType(SkinType.SLIM).decimals(2).unit("%").title("AVERAGE").build();

		// Adding DISK usage of a server

		diskGauge = GaugeBuilder.create().skinType(SkinType.SLIM).decimals(0).title("USED").unit("GBYTE").build();

		// Adding network In/Out bound counts of a server

		netGauge = GaugeBuilder.create().skinType(SkinType.SLIM).decimals(0).unit("BOUNDS").title("IN/OUT").build();

		SVInfoPane.getChildren().add(getGauge("RAM USAGE", Color.rgb(255, 183, 77), ramGauge, 0));
		SVInfoPane.getChildren().add(getGauge("CPU LOAD", Color.rgb(229, 115, 115), cpuGauge, 0));
		SVInfoPane.getChildren().add(getGauge("DISK USAGE", Color.rgb(0, 188, 212), diskGauge, 0));
		SVInfoPane.getChildren().add(getGauge("NET TRAFFIC", Color.rgb(76, 175, 80), netGauge, 0));
		rightPane.getChildren().addAll(logTitle, allLog, statusTitle, SVInfoPane);

		bPane.setRight(rightPane);
	}

	private void buildBottomPane()
	{
		TableView<ResultSet> resultSet;
		try
		{
			resultSet = FXMLLoader.load(getClass().getResource("/fxml/TableView.fxml"));
			resultSet.getStylesheets().add("/css/TableView.css");
		}
		catch (IOException e)
		{
			resultSet = new TableView<ResultSet>();
			e.printStackTrace();
		}
		resultSet.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		resultSet.setFixedCellSize(20.0);
		resultSet.setStyle("-fx-font-size:10px;");
		ObservableList<ResultSet> data = FXCollections.observableArrayList();
		data.add(new ResultSet());
		data.add(new ResultSet());
		data.add(new ResultSet());
		data.add(new ResultSet());
		data.add(new ResultSet());
		ArrayList<TableColumn<ResultSet, String>> resultList = new ArrayList<TableColumn<ResultSet, String>>();
		for (int i = 0; i < 37; ++i)
		{
			resultList.add(new TableColumn<ResultSet, String>("data" + i));
			resultList.get(i).prefWidthProperty().bind(resultSet.widthProperty().multiply(0.04));
			resultList.get(i).setCellValueFactory(new PropertyValueFactory<ResultSet, String>("data" + i));
			resultList.get(i).setResizable(false);
		}
		resultSet.getColumns().addAll(resultList);
		resultSet.setItems(data);
		resultSet.prefHeightProperty()
				.bind(Bindings.size(resultSet.getItems()).multiply(resultSet.getFixedCellSize()).add(45));

		Label bottomTitle = new Label("[Server Response Message]");
		bottomTitle.setTextFill(Color.BURLYWOOD);
		bottomTitle.setFont(new Font(14));
		bottomTitle.setPadding(new Insets(0, 0, 0, 0));
		// bottomTitle.setMinWidth(1225);
		// bottomTitle.setAlignment(Pos.CENTER);

		StackPane sPane = new StackPane();
		sPane.getChildren().add(resultSet);
		Label time = new Label();
		time.setTextFill(Color.BURLYWOOD);
		time.setPadding(new Insets(0, 0, 10, 941));

		class TimeThread extends Thread
		{
			Label time;

			TimeThread(Label time)
			{
				this.time = time;
			}

			@Override
			public void run()
			{
				while (true)
				{
					Platform.runLater(new Runnable()
					{
						public void run()
						{
							String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
									.format(Calendar.getInstance().getTime());
							time.setText("Current time is " + timeStamp);
						}
					});
					try
					{
						sleep(1000);
					}
					catch (InterruptedException e)
					{
						// nothing to do
					}
				}
			}
		}
		timeThread = new TimeThread(time);
		timeThread.start();

		VBox bottom = new VBox(10);
		bottom.setPadding(new Insets(0, 20, 0, 20));
		bottom.getChildren().addAll(bottomTitle, sPane, time);

		bPane.setBottom(bottom);
	}

	private VBox getGauge(final String text, final Color color, final Gauge gauge, double value)
	{
		Rectangle bar = new Rectangle(100, 3);
		bar.setArcHeight(10);
		bar.setArcWidth(10);
		bar.setFill(color);

		Label label = new Label(text);
		label.setTextFill(color);
		label.setAlignment(Pos.CENTER);
		label.setPadding(new Insets(5, 0, 10, 0));

		gauge.setBarEffectEnabled(true);
		gauge.setBarColor(color);
		gauge.setBarBackgroundColor(Color.rgb(75, 75, 75));
		gauge.setAnimated(true);
		gauge.setAnimationDuration(2500);
		gauge.setPrefSize(100, 100);
		gauge.setValue(value);

		VBox vBox = new VBox(bar, label, gauge);
		vBox.setSpacing(1);
		vBox.setAlignment(Pos.CENTER);

		return vBox;
	}

	private Pane getHistory()
	{
		history = new History();
		return history.buildPane();
	}

	private Pane getLogTrace()
	{
		logTrace = new LogTrace();
		return new FlowPane();

	}

	private Pane getErrorTrace()
	{
		errorTrace = new ErrorTrace();
		return new FlowPane();

	}

	public void setResourceStatus(double used, double total, String resource)
	{
		if (resource.equals("ram"))
		{
			ramUsed = used;
			ramTotal = total;
			Platform.runLater(new Runnable()
			{
				public void run()
				{
					ramGauge.setMaxValue(ramTotal / 1000000);
					ramGauge.setValue(ramUsed / 1000000);
				}
			});
		}
		else if (resource.equals("cpu"))
		{
			cpuUsed = used;
			cpuTotal = total; // total==1
			Platform.runLater(new Runnable()
			{
				public void run()
				{
					cpuGauge.setMaxValue(cpuTotal * 100);
					cpuGauge.setValue(cpuUsed * 100);
				}
			});
		}
		else if (resource.equals("disk"))
		{
			diskUsed = used;
			diskTotal = total;
			Platform.runLater(new Runnable()
			{
				public void run()
				{
					diskGauge.setMaxValue(diskTotal / 1000000);
					diskGauge.setValue(diskUsed / 1000000);
				}
			});
		}
		else if (resource.equals("net"))
		{
			netUsed = used;
			netTotal = total; // total=10000;
			Platform.runLater(new Runnable()
			{
				public void run()
				{
					netGauge.setMaxValue(netTotal);
					netGauge.setValue(netUsed);
				}
			});
		}
		else if (resource.equals("eff"))
		{
			efficiency = used;
			Platform.runLater(new Runnable()
			{
				public void run()
				{
					summaryGauge.setValue(efficiency);
				}
			});
		}
	}

	public void setParsingResult(final String[] result)
	{
		reqText.setText(result[0]);
		reqTypeText.setText(result[1]);

		SVModuleText.setText(result[2]);
		if (result[2].length() < 23)
			SVModuleText.setAlignment(Pos.CENTER);
		else
			SVModuleText.setAlignment(Pos.CENTER_LEFT);

		paramText.setText(result[3]);

		// GET or short POST
		if (result[3].length() < 21)
			paramText.setAlignment(Pos.CENTER);
		// long POST
		else
			paramText.setAlignment(Pos.CENTER_LEFT);

		sIdText.setText(result[4]);
		if (result[4].equals("null") || result[4].equals("Unknown ID"))
			sIdText.setAlignment(Pos.CENTER);
		else
			sIdText.setAlignment(Pos.CENTER_LEFT);

		sTimeText.setText(result[5]);
		reqQueryText.setText(result[6]);

		errTypeText.setText(result[7]);
		if (result[7].equals("No Error"))
		{
			errTypeText.setStyle("-fx-text-fill: #0e9b6c;");
			errTypeText.setAlignment(Pos.CENTER);
		}
		if (result[7].length() > 15)
			errTypeText.setAlignment(Pos.CENTER_LEFT);

		errNameText.setText(result[8]);
		if (result[8].equals("No Error"))
		{
			errNameText.setStyle("-fx-text-fill: #0e9b6c;");
			errNameText.setAlignment(Pos.CENTER);
		}
		if (result[8].length() > 15)
			errNameText.setAlignment(Pos.CENTER_LEFT);

		errTimeText.setText(result[9]);
		if (result[9].equals("No Error"))
		{
			errTimeText.setStyle("-fx-text-fill: #0e9b6c;");
			errTimeText.setAlignment(Pos.CENTER);
		}
		if (result[9].length() > 15)
			errTimeText.setAlignment(Pos.CENTER_LEFT);

		Platform.runLater(new Runnable() {

			public void run()
			{
				Tooltip tip1=new Tooltip(result[11]);
				Tooltip tip2=new Tooltip(result[12]);
				Tooltip tip3=new Tooltip(result[13]);
				Tooltip tip4=new Tooltip(result[14]);
				Tooltip tip5=new Tooltip(result[15]);
//				Tooltip tip6=new Tooltip(result[16]);
//				Tooltip tip7=new Tooltip(result[17]);
				
				tomcatLB.setText(result[11]);
				tomcatLB.setTooltip(tip1);
				postgresLB.setText(result[12]);
				postgresLB.setTooltip(tip2);
				springLB.setText(result[13]);
				springLB.setTooltip(tip3);
				restapiLB.setText(result[14]);
				restapiLB.setTooltip(tip4);
				javaLB.setText(result[15]);
				javaLB.setTooltip(tip5);
//				javaClientLB.setText(result[16]);
//				javaClientLB.setTooltip(tip6);
//				dllClientLB.setText(result[17]);
//				dllClientLB.setTooltip(tip7);
			}
		});

		// Display all logs line by line.
		new Thread()
		{
			String[] str = result[10].split("\r\n");
			int i = 0;

			@Override
			public void run()
			{

				for (i = 0; i < str.length; ++i)
				{
					Platform.runLater(new Runnable()
					{
						public void run()
						{
							allLog.setText(sb.append(str[i]).append("\r\n\r\n").toString());
						};
					});
					try
					{
						Thread.sleep(100);
					}
					catch (InterruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	public void setDisconnected()
	{
		status = "Disconnected";
		serverInfo.setText("[Server Info] - " + status);
	}

	public void setConnected(String serverIP)
	{
		status = serverIP;
		serverInfo.setText("[Server Info] - " + status);
	}

	public void setOpStatus(String status)
	{
		if (status.equals("IDLE"))
		{
			opStatusLabel.setTextFill(Color.rgb(255, 255, 0));
			{
				Platform.runLater(new Runnable()
				{

					public void run()
					{
						opStatusLabel.setText("IDLE");
					}
				});
			}
		}
		else if (status.equals("RUN"))
		{
			opStatusLabel.setTextFill(Color.rgb(14, 155, 108));
			Platform.runLater(new Runnable()
			{

				public void run()
				{
					opStatusLabel.setText("RUN");
				}
			});
		}
	}
}
