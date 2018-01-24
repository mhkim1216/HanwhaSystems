/**
 * Created 01.02.2018.
 * Last Modified 01.23.2018.
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

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import enav.monitor.polling.PollingManager;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.Gauge.SkinType;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.Section;

import javafx.application.*;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class RemoteUI extends Application
{
	private Stage primaryStage;
	private TabPane tPane;
	private BorderPane bPane;
	private VBox rPane;
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
	
	private String serverIp;

	public RemoteUI()
	{
		ramTotal = getRamStatus("total");
		ramUsed = getRamStatus("used");
		cpuTotal = 1.0;
		cpuUsed = getCpuStatus();
		diskTotal = getDiskStatus("total");
		diskUsed = getDiskStatus("used");
		netTotal = 1000.0;
		netUsed = getAllBounds();
		serverIp="Not Connected";
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
		setRootPane();
		setScene();
		setStage(primaryStage);
		
		manager=PollingManager.getInstance(this, history, logTrace, errorTrace);
		//add codes for running manager's thread in background
		manager.monitor();	
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
		scene = new Scene(rPane, 1280, 775);
		// scene.setFill(Color.rgb(0, 33, 63, 1));
	}

	private void setStage(Stage stage)
	{
		primaryStage = stage;
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.setTitle("eNavigation DSP Monitor");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void setTabPane()
	{
		// create another tabs
		hPane=getHistory();
		logPane=getLogTrace();
		errPane=getErrorTrace();
		
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
	}

	private void setRootPane()
	{
		rPane = new VBox();
		rPane.setStyle("-fx-background-color: " + rootColor + ";");
		MenuBar menuBar = new MenuBar();
		menuBar.setStyle("-fx-background-color: " + rootColor + ";");
		menuBar.setPadding(new Insets(0, 0, 0, 10));
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
		titleLabel.setStyle("-fx-text-fill: white;");
		titleLabel.setFont(new Font(18));
		titleLabel.setPadding(new Insets(10));
		FlowPane titleBar = new FlowPane();
		FileInputStream fis=null;
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
			if(fis!=null)
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
		rPane.getChildren().add(titleBar);
		rPane.getChildren().add(menuBar);
		rPane.getChildren().add(tPane);
	}

	private void buildLeftPane()
	{
		bPane = new BorderPane();
		bPane.setMaxWidth(1225);
		bPane.setMinHeight(637);
		bPane.setPadding(new Insets(5, 10, 0, 10));

		VBox leftPane = new VBox();
		leftPane.setPadding(new Insets(10, 5, 0, 20));

		Label serverInfo = new Label("[Server Info] - "+serverIp);
		serverInfo.setTextFill(Color.BURLYWOOD);
		serverInfo.setFont(new Font(15));
		serverInfo.setPadding(new Insets(0, 0, -23, 0));

		Gauge summaryGauge = GaugeBuilder.create().skinType(SkinType.SIMPLE).decimals(2).maxValue(100).unit("%")
				.title("USAGE").maxWidth(180).foregroundBaseColor(Color.BURLYWOOD).build();
		summaryGauge.setNeedleColor(Color.DARKGOLDENROD);
		summaryGauge.setValue(getEfficiency() * 100);
		summaryGauge.setBarEffectEnabled(true);
		summaryGauge.setAnimated(true);
		summaryGauge.setAnimationDuration(2500);
		summaryGauge.setPadding(new Insets(0, 0, -20, 20));
		
		summaryGauge.setNeedleColor(Color.rgb(16, 55, 19));
		summaryGauge.addSection(new Section(0, 10, Color.rgb(27,94,32)));
		summaryGauge.addSection(new Section(10, 20, Color.rgb(46,125,50)));
		summaryGauge.addSection(new Section(20, 30, Color.rgb(56,142,60)));
		summaryGauge.addSection(new Section(30, 40, Color.rgb(67,160,71)));
		summaryGauge.addSection(new Section(40, 50, Color.rgb(76,175,80)));
		summaryGauge.addSection(new Section(50, 60, Color.rgb(102,187,106)));
		summaryGauge.addSection(new Section(60, 70, Color.rgb(129,199,132)));
		summaryGauge.addSection(new Section(70, 80, Color.rgb(165,214,167)));
		summaryGauge.addSection(new Section(80, 90, Color.rgb(200,230,201)));
		summaryGauge.addSection(new Section(90, 100, Color.rgb(232,245,233)));
		
		HBox opTitle = new HBox();
		opTitle.setPadding(new Insets(0, 0, 10, 0));
		
		String opStatus=new String("IDLE");
		Label opStatusLabel = new Label(opStatus);
//		opStatusLabel.setStyle("-fx-font-weight: bold;");
		opStatusLabel.setFont(new Font(15));
		
		if(opStatus.equals("IDLE"))
			opStatusLabel.setTextFill(Color.rgb(255, 235, 59));
		else if(opStatus.equals("RUN"))
			opStatusLabel.setTextFill(Color.rgb(76, 175, 80));
		else	
			opStatusLabel.setTextFill(Color.rgb(244, 67, 54));
		
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
		
		TextField errTypeText = new TextField();
		errTypeText.getStylesheets().add("/css/TextField.css");
		errTypeText.setPrefColumnCount(10);
		errTypeText.setEditable(false);
		TextField errNameText = new TextField();
		errNameText.getStylesheets().add("/css/TextField.css");
		errNameText.setPrefColumnCount(10);
		errNameText.setEditable(false);
		TextField errTimeText = new TextField();
		errTimeText.getStylesheets().add("/css/TextField.css");
		errTimeText.setPrefColumnCount(10);
		errTimeText.setEditable(false);
		
		errType.getChildren().addAll(errTypeLabel, errTypeText);
		errName.getChildren().addAll(errNameLabel, errNameText);
		errTime.getChildren().addAll(errTimeLabel, errTimeText);
		
		leftPane.getChildren().addAll(serverInfo, summaryGauge, opTitle, errType, errName, errTime);
		bPane.setLeft(leftPane);
	}

	private void buildTopPane()
	{
		// need delimiter line
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

		reqInfoLabel.setPadding(new Insets(0, 0, 10, 0));
		requestor.setPadding(new Insets(0, 0, 10, 0));
		SVModule.setPadding(new Insets(0, 0, 10, 0));
		reqParam.setPadding(new Insets(0, 0, 10, 0));
		reqType.setPadding(new Insets(0, 0, 10, 0));
		sessionID.setPadding(new Insets(0, 0, 10, 0));
		sessionTime.setPadding(new Insets(0, 0, 10, 0));
		tempSlot.setPadding(new Insets(0, 0, 10, 0));
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
		reqQueryLabel.setPrefWidth(220);
		reqQueryLabel.setFont(new Font(14));

		TextField reqText = new TextField();
		reqText.getStylesheets().add("/css/TextField.css");
		reqText.setPrefColumnCount(16);
		reqText.setEditable(false);
		TextField reqTypeText = new TextField();
		reqTypeText.getStylesheets().add("/css/TextField.css");
		reqTypeText.setPrefColumnCount(16);
		reqTypeText.setEditable(false);
		TextField SVModuleText = new TextField();
		SVModuleText.getStylesheets().add("/css/TextField.css");
		SVModuleText.setPrefColumnCount(16);
		SVModuleText.setEditable(false);
		TextField paramText = new TextField();
		paramText.getStylesheets().add("/css/TextField.css");
		paramText.setPrefColumnCount(16);
		paramText.setEditable(false);
		TextField sIdText = new TextField();
		sIdText.getStylesheets().add("/css/TextField.css");
		sIdText.setPrefColumnCount(16);
		sIdText.setEditable(false);
		TextField sTimeText = new TextField();
		sTimeText.getStylesheets().add("/css/TextField.css");
		sTimeText.setPrefColumnCount(16);
		sTimeText.setEditable(false);
		TextField tempSlotText = new TextField();
		tempSlotText.getStylesheets().add("/css/TextField.css");
		tempSlotText.setPrefColumnCount(16);
		tempSlotText.setEditable(false);
		TextArea reqQueryText = new TextArea();
		reqQueryText.getStylesheets().add("/css/TextArea.css");
		reqQueryText.setPrefRowCount(4);
		reqQueryText.setEditable(false);

		Label[] ok=new Label[7];
		for(int i=0;i<7;++i)
		{
			ok[i]=new Label("PASSED");
			ok[i].setTranslateX(30);
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
		TextArea logArea = new TextArea();
		logArea.getStylesheets().add("/css/TextArea.css");
		logArea.setPrefRowCount(10);

		Label statusTitle = new Label("[Server Status]");
		statusTitle.setTextFill(Color.BURLYWOOD);
		statusTitle.setFont(new Font(15));
		statusTitle.setPadding(new Insets(15, 0, 10, 0));

		HBox SVInfoPane = new HBox(20);
		SVInfoPane.setPadding(new Insets(0, 10, 0, 0));
		// Adding RAM usage of a server

		Gauge ramGauge = GaugeBuilder.create().skinType(SkinType.SLIM).decimals(0).maxValue(ramTotal / 1000000)
				.unit("MBYTE").title("USED").build();

		// Adding CPU usage of a server

		Gauge cpuGauge = GaugeBuilder.create().skinType(SkinType.SLIM).decimals(2).maxValue(cpuTotal * 100).unit("%")
				.title("AVERAGE").build();

		// Adding DISK usage of a server

		Gauge diskGauge = GaugeBuilder.create().skinType(SkinType.SLIM).decimals(0).maxValue(diskTotal / 1000000)
				.title("USED").unit("GBYTE").build();

		// Adding network In/Out bound counts of a server

		Gauge netGauge = GaugeBuilder.create().skinType(SkinType.SLIM).decimals(0).maxValue(netTotal).unit("BOUNDS")
				.title("IN/OUT").build();

		SVInfoPane.getChildren().add(getGauge("RAM USAGE", Color.rgb(255, 183, 77), ramGauge, ramUsed / 100000));
		SVInfoPane.getChildren().add(getGauge("CPU LOAD", Color.rgb(229, 115, 115), cpuGauge, cpuUsed * 100));
		SVInfoPane.getChildren().add(getGauge("DISK USAGE", Color.rgb(0, 188, 212), diskGauge, diskUsed / 1000000));
		SVInfoPane.getChildren().add(getGauge("NET TRAFFIC", Color.rgb(76, 175, 80), netGauge, netUsed));
		rightPane.getChildren().addAll(logTitle, logArea, statusTitle, SVInfoPane);

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

	private long getRamStatus(String type)
	{
		long result = 0;

		Sigar sigar = new Sigar();
		Mem mem = null;
		try
		{
			mem = sigar.getMem();
		}
		catch (SigarException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (type.equals("total"))
			result = mem.getActualFree();

		else if (type.equals("used"))
			result = mem.getActualUsed();

		else
			result = 0l;

		return result;

	}

	private double getCpuStatus()
	{
		Sigar sigar = new Sigar();
		CpuPerc cpu = null;
		double result;

		try
		{
			cpu = sigar.getCpuPerc();
		}
		catch (SigarException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			result = cpu.getUser();
		}

		return result;
	}

	private long getDiskStatus(String type)
	{
		Sigar sigar = new Sigar();
		FileSystemUsage disk = null;
		long result;

		try
		{
			disk = sigar.getFileSystemUsage("C:\\");
		}
		catch (SigarException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (type.equals("total"))
			result = disk.getTotal();

		else if (type.equals("used"))
			result = disk.getUsed();
		else
			result = 0l;

		return result;
	}

	private int getAllBounds()
	{
		Sigar sigar = new Sigar();
		int result = 0;

		try
		{
			result = sigar.getNetStat().getTcpOutboundTotal() + sigar.getNetStat().getTcpInboundTotal();
		}
		catch (SigarException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	private double getEfficiency()
	{
		efficiency = (ramUsed / ramTotal + cpuUsed / cpuTotal + diskUsed / diskTotal + netUsed / netTotal) / 4;

		return efficiency;
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
		history=new History();
		return history.buildPane();
	}
	
	private Pane getLogTrace()
	{
		logTrace=new LogTrace();
		return new FlowPane();
		
	}
	
	private Pane getErrorTrace()
	{
		errorTrace=new ErrorTrace();
		return new FlowPane();
		
	}
}
