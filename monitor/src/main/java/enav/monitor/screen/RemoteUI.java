/**
 * Created 01.02.2018.
 * Layout for remote screen has been built using JavaFX.
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

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.Gauge.SkinType;
import eu.hansolo.medusa.GaugeBuilder;

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
	
	private static String rootColor = "#1b221b";
	String textFieldStyle="-fx-effect: innershadow(three-pass-box, gray, 5, 0.3, 1, 1);-fx-background-color:#e6efe3;";
	
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
		String[] tabLabel = new String[] { "Connection", "Database", "Graph" };
		// Assign Layout Pane
		Pane[] tabContent = new Pane[] { bPane, new FlowPane(), new FlowPane() };
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
		Label titleLabel = new Label("e-Navigation DSP Monitor 0.1 ");
		titleLabel.setStyle("-fx-text-fill: white;");
		titleLabel.setFont(new Font(18));
		titleLabel.setPadding(new Insets(10));
		FlowPane titleBar = new FlowPane();
		FileInputStream fis;
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
		AnchorPane.setLeftAnchor(exitButton, 850.0);
		AnchorPane.setTopAnchor(minButton, 13.0);
		AnchorPane.setLeftAnchor(minButton, 826.0);
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
		bPane.setMinWidth(1225);
		bPane.setMinHeight(543);
		VBox lPane = new VBox();
		lPane.setPadding(new Insets(10, 20, 20, 20));
		Label reqInfoLabel = new Label("[Request Information]");
		
		HBox requestor = new HBox();
		HBox reqType = new HBox();
		HBox SVModule = new HBox();
		HBox reqParam = new HBox();
		HBox sessionID = new HBox();
		HBox sessionTime=new HBox();
		
		reqInfoLabel.setPadding(new Insets(0, 0, 10, 0));
		requestor.setPadding(new Insets(0, 0, 10, 0));
		SVModule.setPadding(new Insets(0, 0, 10, 0));
		reqParam.setPadding(new Insets(0, 0, 10, 0));
		reqType.setPadding(new Insets(0, 0, 10, 0));
		sessionID.setPadding(new Insets(0, 0, 10, 0));
		sessionTime.setPadding(new Insets(0, 0, 10, 0));
		
		reqInfoLabel.setTextFill(Color.BURLYWOOD);
		reqInfoLabel.setFont(new Font(14));

		Label reqLabel = new Label("Requestor");
		reqLabel.setTextFill(Color.WHITE);
		reqLabel.setMinWidth(Region.USE_COMPUTED_SIZE);
		reqLabel.setPrefWidth(140);
		reqLabel.setFont(new Font(14));

		Label reqTypeLabel = new Label("Request Type");
		reqTypeLabel.setTextFill(Color.WHITE);
		reqTypeLabel.setMinWidth(Region.USE_COMPUTED_SIZE);
		reqTypeLabel.setPrefWidth(140);
		reqTypeLabel.setFont(new Font(14));

		Label SVModuleLabel = new Label("Service Module");
		SVModuleLabel.setTextFill(Color.WHITE);
		SVModuleLabel.setMinWidth(Region.USE_PREF_SIZE);
		SVModuleLabel.setPrefWidth(140);
		SVModuleLabel.setFont(new Font(14));

		Label paramLabel = new Label("Parameter");
		paramLabel.setTextFill(Color.WHITE);
		paramLabel.setMinWidth(Region.USE_PREF_SIZE);
		paramLabel.setPrefWidth(140);
		paramLabel.setFont(new Font(14));
		
		Label sIdLabel = new Label("Session ID");
		sIdLabel.setTextFill(Color.WHITE);
		sIdLabel.setMinWidth(Region.USE_PREF_SIZE);
		sIdLabel.setPrefWidth(140);
		sIdLabel.setFont(new Font(14));

		Label sTimeLabel = new Label("Session Time");
		sTimeLabel.setTextFill(Color.WHITE);
		sTimeLabel.setMinWidth(Region.USE_PREF_SIZE);
		sTimeLabel.setPrefWidth(140);
		sTimeLabel.setFont(new Font(14));
		
		TextField reqText = new TextField();
		reqText.setStyle(textFieldStyle);
		reqText.setEditable(false);
		TextField reqTypeText = new TextField();
		reqTypeText.setStyle(textFieldStyle);
		reqTypeText.setEditable(false);
		TextField SVModuleText = new TextField();
		SVModuleText.setStyle(textFieldStyle);
		SVModuleText.setEditable(false);
		TextField paramText = new TextField();
		paramText.setStyle(textFieldStyle);
		paramText.setEditable(false);
		TextField sIdText = new TextField();
		sIdText.setStyle(textFieldStyle);
		sIdText.setEditable(false);
		TextField sTimeText = new TextField();
		sTimeText.setStyle(textFieldStyle);
		sTimeText.setEditable(false);

		requestor.getChildren().addAll(reqLabel, reqText);
		reqType.getChildren().addAll(reqTypeLabel, reqTypeText);
		SVModule.getChildren().addAll(SVModuleLabel, SVModuleText);
		reqParam.getChildren().addAll(paramLabel, paramText);
		sessionID.getChildren().addAll(sIdLabel, sIdText);
		sessionTime.getChildren().addAll(sTimeLabel, sTimeText);
		
		lPane.getChildren().addAll(reqInfoLabel, requestor, reqType, SVModule, reqParam, sessionID, sessionTime);
		bPane.setLeft(lPane);
	}

	private void buildTopPane()
	{
		// need delimiter line
	}

	private void buildCenterPane()
	{

	}

	private void buildRightPane()
	{
		VBox rightPane = new VBox();
		rightPane.setPadding(new Insets(10, 20, 20, 20));

		Label logTitle = new Label("[Server Logs]");
		logTitle.setTextFill(Color.BURLYWOOD);
		logTitle.setFont(new Font(14));
		logTitle.setPadding(new Insets(0, 0, 13, 0));
		TextArea logArea = new TextArea();
		logArea.setStyle(textFieldStyle);
		logArea.setPrefRowCount(10);
		logArea.setEditable(false);

		Label statusTitle = new Label("[Server Status]");
		statusTitle.setTextFill(Color.BURLYWOOD);
		statusTitle.setFont(new Font(14));
		statusTitle.setPadding(new Insets(15, 0, 10, 0));

		HBox SVInfoPane = new HBox(20);
		SVInfoPane.setPadding(new Insets(0, 10, 0, 0));
		// Adding RAM usage of a server

		Gauge ramGauge = GaugeBuilder.create().skinType(SkinType.SLIM).decimals(0)
				.maxValue(getRamStatus("total") / 1000000).unit("MBYTE").title("USED").build();

		// Adding CPU usage of a server

		Gauge cpuGauge = GaugeBuilder.create().skinType(SkinType.SLIM).decimals(2).maxValue(100).unit("%")
				.title("AVERAGE").build();

		// Adding DISK usage of a server

		Gauge diskGauge = GaugeBuilder.create().skinType(SkinType.SLIM).decimals(0)
				.maxValue(getDiskStatus("total") / 1000000).title("USED").unit("GBYTE").build();

		// Adding network In/Out bound counts of a server

		Gauge netGauge = GaugeBuilder.create().skinType(SkinType.SLIM).decimals(0).maxValue(999).unit("BOUNDS")
				.title("IN/OUT").build();

		// StackPane bounds = new StackPane(barChart);

		SVInfoPane.getChildren()
				.add(getGauge("RAM USAGE", Color.rgb(255, 183, 77), ramGauge, getRamStatus("used") / 100000));
		SVInfoPane.getChildren().add(getGauge("CPU LOAD", Color.rgb(229, 115, 115), cpuGauge, getCpuStatus() * 100));
		SVInfoPane.getChildren()
				.add(getGauge("DISK USAGE", Color.rgb(0, 188, 212), diskGauge, getDiskStatus("used") / 1000000));
		SVInfoPane.getChildren().add(getGauge("NET TRAFFIC", Color.rgb(76, 175, 80), netGauge, getAllBounds()));
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
		bottomTitle.setPadding(new Insets(0, 0, 10, 0));
		// bottomTitle.setMinWidth(1225);
		// bottomTitle.setAlignment(Pos.CENTER);

		StackPane sPane = new StackPane();
		sPane.setPadding(new Insets(0, 0, 10, 0));
		sPane.getChildren().add(resultSet);
		Label time = new Label();
		time.setTextFill(Color.BURLYWOOD);
		time.setPadding(new Insets(0, 0, 10, 961));
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

		VBox bottom = new VBox();
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
		{
			result = mem.getActualFree();
			return result;
		}
		else if (type.equals("used"))
		{
			result = mem.getActualUsed();
			return result;
		}
		else
			return 0l;

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
		int result=0;

		try
		{
			result = sigar.getNetStat().getTcpOutboundTotal()+sigar.getNetStat().getTcpInboundTotal();
		}
		catch (SigarException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
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
		gauge.setAnimationDuration(1000);
		gauge.setPrefSize(100, 100);
		gauge.setValue(value);

		VBox vBox = new VBox(bar, label, gauge);
		vBox.setSpacing(1);
		vBox.setAlignment(Pos.CENTER);
		return vBox;
	}
}
