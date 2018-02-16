/**
 * Created 02.11.2018.
 * Last Modified 02.11.2018.
 * Layout for log trace screen has been built using JavaFX.
 * 
 * 
 */

package enav.monitor.screen;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import enav.monitor.polling.Client;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class LogTrace
{
	private ObservableList<Trace> logs;
	private VBox rPane;
	private TableView<Trace> tableView;
	private FileInputStream fis;
	private String requester;
	private TextField requesterTF;
	private ObservableList<String> options;
	private ComboBox<String> comboBox;
	private Map<String, Client> clients;

	public LogTrace()
	{
		clients = new HashMap<String, Client>();
	}

	public void updateTable(Client client)
	{
		try
		{
			requester = client.getName();
			requesterTF.setText(this.requester);

			if (!comboBox.getItems().contains(requester))
				options.add(requester);

//			comboBox.setItems(options);
			comboBox.setValue(comboBox.getPromptText());
			requesterTF.setText(requester);
			// initialize logs.
			logs.clear();

			for (Trace trace : client.getSqlList())
				logs.add(trace);

			clients.put(requester, client);
			tableView.setItems(logs);
		}
		catch (Exception e)
		{

		}
	}

	public Pane buildTablePane()
	{
		rPane = new VBox(12);
		rPane.setPadding(new Insets(20));
		getLogTitle();

		tableView = new TableView<Trace>();
		StackPane sPane = new StackPane(tableView);
		// resultSet.getStylesheets().add("/css/???.css");

		logs = FXCollections.observableArrayList();

		tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		tableView.setFixedCellSize(25);
		tableView.getStylesheets().add("/css/TableView.css");
				
		// Column setting
		ArrayList<TableColumn<Trace, String>> column = new ArrayList<TableColumn<Trace, String>>();
		
		column.add(new TableColumn<Trace, String>("Connected"));
		column.add(new TableColumn<Trace, String>("Disconnected"));
		column.add(new TableColumn<Trace, String>("Query"));

		column.get(0).prefWidthProperty().bind(tableView.widthProperty().multiply(0.12));
		column.get(1).prefWidthProperty().bind(tableView.widthProperty().multiply(0.12));
		column.get(2).prefWidthProperty().bind(tableView.widthProperty().multiply(0.756));
		
		

		column.get(0).setCellValueFactory(new PropertyValueFactory<Trace, String>("FirstSession"));
		column.get(1).setCellValueFactory(new PropertyValueFactory<Trace, String>("LastSession"));
		column.get(2).setCellValueFactory(new PropertyValueFactory<Trace, String>("Sql"));
		// column.get(i).setResizable(false);

		tableView.getColumns().addAll(column);

		// tableView.setItems(logs);
		 tableView.prefHeightProperty()
		 .bind(Bindings.size(tableView.getItems()).multiply(tableView.getFixedCellSize()).add(560));

		rPane.getChildren().add(sPane);
		return rPane;
	}

	private void getLogTitle()
	{
		HBox titlePane = new HBox(10);

		Image titleImage;
		ImageView imageView = null;
		try
		{
			fis = new FileInputStream("src/main/resources/images/trace.png");
			titleImage = new Image(fis); 
			imageView = new ImageView(titleImage);
			imageView.setFitHeight(30);
			imageView.setFitWidth(30);
			imageView.setPreserveRatio(true);
			imageView.setTranslateY(-2);
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
					e.printStackTrace();
				}
		}

		Label requesterLB = new Label("All Traces of Requester ");
		requesterLB.setTextFill(Color.BURLYWOOD);
		requesterLB.setFont(Font.font(16));
		requesterTF = new TextField();
		requesterTF.setEditable(false);
		requesterTF.setFont(Font.font(16));
		requesterTF.setStyle("-fx-text-fill: #dec85f;");
		requesterTF.getStylesheets().add("/css/TextField.css");
		requesterTF.setTranslateY(-2);
		requesterTF.setAlignment(Pos.CENTER);
		requesterTF.setPadding(new Insets(3));

		options = FXCollections.observableArrayList();
		comboBox = new ComboBox<String>(options);
		comboBox.setPromptText("Select Requester");
		comboBox.setTranslateX(605);
		comboBox.getStylesheets().add("/css/ComboBox.css");
		comboBox.setOnAction(new EventHandler<ActionEvent>()
		{

			public void handle(ActionEvent event)
			{
				if(!comboBox.getValue().equals(comboBox.getPromptText()))
					updateTable(clients.get(comboBox.getValue()));
			}
		});

		titlePane.getChildren().addAll(imageView, requesterLB, requesterTF, comboBox);
		rPane.getChildren().add(titlePane);
	}
}
