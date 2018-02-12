/**
 * Created 01.23.2018.
 * Last Modified 02.10.2018.
 * Layout for history screen has been built using JavaFX.
 * 
 * 
 */

package enav.monitor.screen;

import java.util.HashMap;
import java.util.Map;

import enav.monitor.polling.Client;
import enav.monitor.polling.PollingManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

public class History
{
	private VBox rPane;

	private final static int columnWidth = 180;
	private static boolean isFirst = true;
	private HBox aClient;
	Map<String, HBox> clientList;
	private LogTrace logTrace;

	public History(LogTrace logTrace)
	{
		clientList = new HashMap<String, HBox>();
		this.logTrace = logTrace;
	}

	// only can be invoked in PollingManager
	public void addClient(Client client)
	{
		client.increaseUsingCount();
		addRowPane(client);
	}

	public Pane buildPane()
	{
		rPane = new VBox(10);
		rPane.setPadding(new Insets(20, 30, 20, 30));
		rPane.setMaxWidth(1225);
		rPane.setMinHeight(637);
		Label title = new Label("[Client History]");
		title.setTextFill(Color.BURLYWOOD);
		title.setFont(Font.font(15));
		rPane.getChildren().addAll(title);

		if (isFirst)
		{
			rPane.getChildren().add(getTitleRow());
			isFirst = false;
		}

		return rPane;
	}

	// only can be invoked in privately
	private HBox getTitleRow()
	{
		HBox titleRow = new HBox(10);
		titleRow.setPadding(new Insets(20, 0, 0, 0));

		Label column1 = new Label("Client");
		column1.setFont(Font.font(16));
		column1.setTextFill(Color.WHITE);
		column1.setPrefWidth(columnWidth);
		column1.setAlignment(Pos.CENTER);
		Label column2 = new Label("First Session");
		column2.setFont(Font.font(16));
		column2.setTextFill(Color.WHITE);
		column2.setPrefWidth(columnWidth * 0.69);
		column2.setAlignment(Pos.CENTER);
		Label column3 = new Label("Last Session");
		column3.setFont(Font.font(16));
		column3.setTextFill(Color.WHITE);
		column3.setPrefWidth(columnWidth * 1.29);
		column3.setAlignment(Pos.CENTER);
		Label column4 = new Label("Period");
		column4.setFont(Font.font(16));
		column4.setTextFill(Color.WHITE);
		column4.setPrefWidth(columnWidth * 0.37);
		column4.setAlignment(Pos.CENTER);
		column4.setTranslateX(-20);
		Label column5 = new Label("Count");
		column5.setFont(Font.font(16));
		column5.setTextFill(Color.WHITE);
		column5.setPrefWidth(columnWidth * 0.43);
		column5.setAlignment(Pos.CENTER);
		Label column6 = new Label("Query");
		column6.setFont(Font.font(16));
		column6.setTextFill(Color.WHITE);
		column6.setPrefWidth(columnWidth * 1.75);
		column6.setAlignment(Pos.CENTER);

		titleRow.getChildren().addAll(column1, column2, column3, column4, column5, column6);

		return titleRow;
	}

	// only can be invoked in privately
	private void addRowPane(final Client client)
	{
		if (!clientList.containsKey(client.getName()))
		{
			aClient = new HBox(10);

			Circle circle = new Circle();
			circle.setRadius(10);
			circle.setTranslateY(5);
			;
			InnerShadow innerShadow = new InnerShadow(13, Color.BLACK);
			circle.setEffect(innerShadow);

			if (!client.isRunning())
				circle.setFill(Color.rgb(244, 67, 5));
			else
				circle.setFill(Color.rgb(76, 175, 80));

			Label clientLabel = new Label(client.getName());
			clientLabel.setPrefWidth(125);
			clientLabel.setAlignment(Pos.CENTER);
			clientLabel.setFont(Font.font(15));
			clientLabel.setTextFill(Color.WHITE);
			clientLabel.setPadding(new Insets(5, 0, 0, 0));
			TextField field1 = new TextField();
			field1.setPrefWidth(columnWidth);
			field1.getStylesheets().add("/css/TextField.css");
			field1.setText(client.getInitSession());
			field1.setAlignment(Pos.CENTER);
			field1.setFont(Font.font(14));
			TextField field2 = new TextField();
			field2.setPrefWidth(columnWidth);
			field2.getStylesheets().add("/css/TextField.css");
			field2.setText(client.getLastSession());
			field2.setAlignment(Pos.CENTER);
			field2.setFont(Font.font(14));
			TextField field3 = new TextField();
			field3.setPrefWidth(columnWidth * 0.50);
			field3.getStylesheets().add("/css/TextField.css");
			field3.setText(client.getUsingTime());
			field3.setAlignment(Pos.CENTER);
			field3.setFont(Font.font(14));
			TextField field4 = new TextField();
			field4.setPrefWidth(columnWidth * 0.50);
			field4.getStylesheets().add("/css/TextField.css");
			field4.setText(String.valueOf(client.getUsingCount()));
			field4.setAlignment(Pos.CENTER);
			field4.setFont(Font.font(14));
			TextField field5 = new TextField();
			field5.setPrefWidth(columnWidth * 1.75);
			field5.getStylesheets().add("/css/TextField.css");
			field5.setText(String.valueOf(client.getQuery()));
			if (field5.getText().contains("No SQL"))
				field5.setAlignment(Pos.CENTER);
			else
				field5.setAlignment(Pos.CENTER_LEFT);
			field5.setFont(Font.font(14));

			Button traceBtn = new Button("TRACE ALL");
			traceBtn.getStylesheets().add("/css/Button.css");
			traceBtn.setOnAction(new EventHandler<ActionEvent>()
			{
				public void handle(ActionEvent event)
				{
					logTrace.updateTable(
							PollingManager.getInstance().getClientList().get(client.getName()).getSqlList());
				}
			});

			aClient.getChildren().addAll(circle, clientLabel, field1, field2, field3, field4, field5, traceBtn);
			clientList.put(client.getName(), aClient);
			Platform.runLater(new Runnable()
			{

				public void run()
				{
					rPane.getChildren().add(aClient);
				}
			});
		}
		else
		{
			HBox aClient = clientList.get(client.getName());
			((TextField) aClient.getChildren().get(2)).setText(client.getInitSession());
			((TextField) aClient.getChildren().get(3)).setText(client.getLastSession());
			((TextField) aClient.getChildren().get(4)).setText(client.getUsingTime());
			((TextField) aClient.getChildren().get(5)).setText(String.valueOf(client.getUsingCount()));
			((TextField) aClient.getChildren().get(6)).setText(client.getQuery());
		}

	}

}
