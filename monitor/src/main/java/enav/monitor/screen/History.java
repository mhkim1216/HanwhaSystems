/**
 * Created 01.23.2018.
 * Last Modified 01.23.2018.
 * Layout for history screen has been built using JavaFX.
 * 
 * 
 */

package enav.monitor.screen;

import java.util.ArrayList;
import java.util.List;

import enav.monitor.polling.Client;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

public class History
{
	private VBox rPane;
	
	private final static int columnWidth = 220;
	private static int clientCount = 0;
	private static boolean isFirst = true;
	List<Client> clientList;

	public History()
	{
		clientList = new ArrayList<Client>();

	}

	// only can be invoked in PollingManager
	public void addClient(Client client)
	{
		clientCount++;
		clientList.add(client);
		addRowPane(client);
	}

	public Pane buildPane()
	{
		rPane = new VBox(10);
		rPane.setPadding(new Insets(20));
		rPane.setMaxWidth(1225);
		rPane.setMinHeight(637);
		Label title = new Label("[Client History]");
		title.setTextFill(Color.BURLYWOOD);
		title.setFont(Font.font(14));
		rPane.getChildren().addAll(title);
		
		if (isFirst)
		{
			rPane.getChildren().add(getTitleRow());
			isFirst=false;
		}
		
		return rPane;
	}

	// only can be invoked in privately
	private HBox getTitleRow()
	{
		HBox titleRow = new HBox(15);

		Label column1 = new Label();
		column1.setPrefWidth(150);
		Label column2 = new Label("First Session");
		column2.setFont(Font.font(16));
		column2.setTextFill(Color.WHITE);
		column2.setPrefWidth(columnWidth);
		Label column3 = new Label("Last Session");
		column3.setFont(Font.font(16));
		column3.setTextFill(Color.WHITE);
		column3.setPrefWidth(columnWidth);
		Label column4 = new Label("Total Usage Time");
		column4.setFont(Font.font(16));
		column4.setTextFill(Color.WHITE);
		column4.setPrefWidth(columnWidth);
		Label column5 = new Label("Total Usage Count");
		column5.setFont(Font.font(16));
		column5.setTextFill(Color.WHITE);
		column5.setPrefWidth(columnWidth);
		Label column6 = new Label();
		column6.setFont(Font.font(16));
		column6.setTextFill(Color.WHITE);
		column6.setPrefWidth(columnWidth);
		titleRow.getChildren().addAll(column1, column2, column3, column4, column5, column6);

		return titleRow;
	}

	// only can be invoked in privately
	private void addRowPane(Client client)
	{
		HBox aClient = new HBox(15);

		Circle circle = new Circle();
		circle.setRadius(20);
		if (!client.isRunning())
			circle.setFill(Color.rgb(244, 67, 5));
		else
			circle.setFill(Color.rgb(76, 175, 80));

		Label clientLabel = new Label(client.getName());
		clientLabel.setPrefWidth(150);
		TextField field1 = new TextField();
		field1.setPrefWidth(columnWidth);
		TextField field2 = new TextField();
		field2.setPrefWidth(columnWidth);
		TextField field3 = new TextField();
		field3.setPrefWidth(columnWidth);
		TextField field4 = new TextField();
		field4.setPrefWidth(columnWidth);
		Button traceBtn = new Button("trace");

		aClient.getChildren().addAll(circle, clientLabel, field1, field2, field3, field4, traceBtn);
		rPane.getChildren().add(aClient);
	}

}
