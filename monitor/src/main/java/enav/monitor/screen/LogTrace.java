/**
 * Created 02.11.2018.
 * Last Modified 02.11.2018.
 * Layout for log trace screen has been built using JavaFX.
 * 
 * 
 */

package enav.monitor.screen;

import java.util.ArrayList;
import java.util.List;

import enav.monitor.polling.Client;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class LogTrace
{
	private ObservableList<Trace> logs;
	private VBox rPane;
	private TableView<Trace> tableView;
	
	public LogTrace()
	{
		
	}
	
	public void updateTable(List<Trace> traces)
	{
		// initialize logs.
		logs.clear();
		
		for(Trace trace:traces)
			logs.add(trace);

		tableView.setItems(logs);
	}

	public Pane buildTablePane()
	{
		VBox rPane = new VBox();

		tableView = new TableView<Trace>();
		// resultSet.getStylesheets().add("/css/???.css");

		logs=FXCollections.observableArrayList();
		
		tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		tableView.setFixedCellSize(20.0);

		// Column setting
		ArrayList<TableColumn<Trace, String>> column = new ArrayList<TableColumn<Trace, String>>();

		column.add(new TableColumn<Trace, String>("Connected"));
		column.add(new TableColumn<Trace, String>("Disconnected"));
		column.add(new TableColumn<Trace, String>("Query"));

		column.get(0).prefWidthProperty().bind(tableView.widthProperty().multiply(0.10));
		column.get(1).prefWidthProperty().bind(tableView.widthProperty().multiply(0.10));
		column.get(2).prefWidthProperty().bind(tableView.widthProperty().multiply(0.80));

		column.get(0).setCellValueFactory(new PropertyValueFactory<Trace,String>("FirstSession"));
		column.get(1).setCellValueFactory(new PropertyValueFactory<Trace,String>("LastSession"));
		column.get(2).setCellValueFactory(new PropertyValueFactory<Trace,String>("Sql"));
		// column.get(i).setResizable(false);

		tableView.getColumns().addAll(column);
		
//		tableView.setItems(logs);
//		tableView.prefHeightProperty()
//				.bind(Bindings.size(tableView.getItems()).multiply(tableView.getFixedCellSize()).add(25));

		rPane.getChildren().add(tableView);
		return rPane;
	}

}
