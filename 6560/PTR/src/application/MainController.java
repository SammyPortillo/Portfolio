package application;

import java.io.File;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class MainController implements Initializable {
	ObservableList<String> olistPage;
	ObservableList<String> olistFrame;
	ObservableList<String> olistVictims;
	ObservableList<String> olistHits;

	ArrayList<String> page = new ArrayList<String>();
	ArrayList<String> frame = new ArrayList<String>();
	ArrayList<String> hd;
	@FXML
	Button btnStart;
	@FXML
	Button btnCancel;
	@FXML
	Button btnReset;
	@FXML
	TextField txtProgramSize;
	@FXML
	TextField txtRAMSize;
	@FXML
	TextField txtSectorSize;
	@FXML
	ListView<String> listPages;
	@FXML
	ListView<String> listHD;
	@FXML
	ListView<String> listVictims;
	@FXML
	ListView<String> listHits;
	@FXML
	ProgressIndicator progressIndicator;
	@FXML
	Slider slider;
	@FXML
	TextField sliderText;

	MediaPlayer mp;
	Media greeting;
	Media soundMiss;
	Media soundHit;
	Media soundVictim;
	Media soundCancel;

	Service<Integer> backgroundThread;
	static final double INIT_SLIDER_VALUE = 1000;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// listRam.setSelectionModel(MultipleSelectionModel<String> 3);
		txtSectorSize.setText("16");

		slider.setValue(INIT_SLIDER_VALUE);
		sliderText.setText(new Double(INIT_SLIDER_VALUE).toString());
		sliderText.textProperty().bindBidirectional(slider.valueProperty(), NumberFormat.getNumberInstance());

		String path = new File("src/media/life.wav").getAbsolutePath();
		greeting = new Media(new File(path).toURI().toString());

		path = new File("src/media/shotgun-mossberg.wav").getAbsolutePath();
		soundCancel = new Media(new File(path).toURI().toString());

		mp = new MediaPlayer(greeting);
		mp.setAutoPlay(true);
	}

	public void onClickStart(ActionEvent event) {
		listPages.setItems(null);
		listVictims.setItems(null);
		listHits.setItems(null);
		btnCancel.setVisible(true);

		if (txtProgramSize.getText().isEmpty())
			txtProgramSize.setText("10");

		if (txtRAMSize.getText().isEmpty())
			txtRAMSize.setText("10");

		int size = Integer.parseInt(txtProgramSize.getText());
		int ram = Integer.parseInt(txtRAMSize.getText());
		int sector = Integer.parseInt(txtSectorSize.getText());

		String path = new File("src/media/error.wav").getAbsolutePath();
		soundMiss = new Media(new File(path).toURI().toString());

		path = new File("src/media/Realistic_Punch.wav").getAbsolutePath();
		soundHit = new Media(new File(path).toURI().toString());

		path = new File("src/media/sniper.wav").getAbsolutePath();
		soundVictim = new Media(new File(path).toURI().toString());

		backgroundThread = new Service<Integer>() {
			@Override
			protected Task<Integer> createTask() {
				return new Task<Integer>() {

					@Override
					protected Integer call() throws Exception {
						Program p = new Program(size, ram, sector);
						int x;
						for (x = 0; x < size; x++) {

							if (isCancelled())
								break;

							int addRecord = 0;
							do {
								addRecord = p.cycle();

								System.out.println(addRecord);
								page = p.updatePage();
								olistPage = FXCollections.observableArrayList(page);
								listPages.setItems(olistPage);
								olistVictims = FXCollections.observableArrayList(p.victims);
								listVictims.setItems(olistVictims);
								olistHits = FXCollections.observableArrayList(p.hits);
								listHits.setItems(olistHits);
								updateProgress(x, size);

								if (addRecord == 1)
									mp = new MediaPlayer(soundMiss);
								else if (addRecord == 2)
									mp = new MediaPlayer(soundVictim);
								else
									mp = new MediaPlayer(soundHit);
								mp.setAutoPlay(true);

								try {
									Thread.sleep((long) slider.getValue());
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							} while (addRecord == 3);
						}

						if (!isCancelled())
							updateProgress(size, size);

						return x;
					}
				};
			} // End of createTask

		};
		backgroundThread.setOnCancelled(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent event) {
				backgroundThread.cancel();
			}
		});

		backgroundThread.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				System.out.println("Complete !");
			}
		});

		progressIndicator.progressProperty().bind(backgroundThread.progressProperty());
		backgroundThread.restart();
	}

	public void onClickCancel(ActionEvent event) {
		backgroundThread.cancel();

		mp = new MediaPlayer(soundCancel);
		mp.setAutoPlay(true);

		System.out.println("Cancelled Thread");
		btnCancel.setVisible(false);
	}

}