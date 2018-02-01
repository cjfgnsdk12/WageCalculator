package view;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import model.cost;
import model.factory;

public class ViewerController implements Initializable{

	@FXML private TextField input_name;
	@FXML private TextField input_cost;
	@FXML private TableView<cost> table; 
	@FXML private TableColumn<cost, String> nameCol;
	@FXML private TableColumn<cost, String> costCol;
	@FXML private TextArea textArea;
		
	private File selectedExcel;
	private ArrayList<cost> wageUnit=new ArrayList<>();
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		initTableView();
	}
	
	public void insert_cost(ActionEvent event) {
		if(input_name.getText().equals("")==true || input_cost.getText().equals("")==true) {
			blankAlert();
		}else {
			cost cost=new cost(new SimpleStringProperty( input_name.getText()) ,new SimpleStringProperty( input_cost.getText()));
			input_cost.setText("");
			input_name.setText("");
			table.getItems().add(cost);
			
		}
	}
	public void delete_cost(ActionEvent event) {
		table.getItems().remove(table.getSelectionModel().getSelectedIndex());
	}
	
	/**
	 * 테이블 뷰 리스너와 값넣을수 있게 초기화하는 메소드
	 */
	public void initTableView() {
		factory reader=new factory();
		//ArrayList<cost> wageUnit=new ArrayList<>();
		reader.loadConfig(wageUnit);
		for(int i=0; i<wageUnit.size();i++) {
			table.getItems().add(wageUnit.get(i));
		}
		
		
		nameCol.setCellValueFactory(cellData->cellData.getValue().nameProperty());
		costCol.setCellValueFactory(cellData->cellData.getValue().costProperty());
		table.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<cost>() {

			@Override
			public void changed(ObservableValue<? extends cost> observable, cost oldValue, cost newValue) {
				// TODO Auto-generated method stub
			}
			
		});
	}
	/**
	 * 경고문 띄우는 메소드
	 */
	public void blankAlert() {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("경고 입니다.");
		alert.setHeaderText("엄마 밑에 확인해봐");
		alert.setContentText("공백이잖아 ㅎㅎ");
		alert.showAndWait();
	}
	/**
	 * 가격과 단가를 저장해주는 파일
	 */
	public void saveConfig() {
		factory reader=new factory();
		ArrayList<cost> wageUnit=new ArrayList<cost>();
		for(int i=0;i<table.getItems().size();i++) {
			wageUnit.add(table.getItems().get(i));
		}
		reader.saveConfig(wageUnit);
	}
	/**
	 * 엑셀 읽기
	 */
	public void loadExcel() {
		FileChooser fileChoser=new FileChooser();
		fileChoser.getExtensionFilters().add(new ExtensionFilter("excel Files", "*.xls"));
		selectedExcel=fileChoser.showOpenDialog(new Stage());
		if(selectedExcel !=null) {
			//selectedFilePath=selectedExcel.getPath();
			String name=selectedExcel.getName();
			textArea.appendText(name+"을 읽어드렸습니다.\n");
		}else {
			textArea.appendText("파일 읽기 실패\n");
		}
		
	}
	/**
	 * 엑셀 변환하기
	 */
	public void convertExcel() {
		if(selectedExcel!=null) {
		FileInputStream fis=null;
		HSSFWorkbook workbook=null;
		String name=selectedExcel.getName();
		try {
			fis= new FileInputStream(selectedExcel);
			POIFSFileSystem poi=new POIFSFileSystem(fis);
			workbook=new HSSFWorkbook(poi);
			fis.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		factory fac=new factory();
		textArea.appendText(fac.convert(workbook,wageUnit,name));  
		}else {
			textArea.appendText("엑셀을  못읽었습니다.\n");
		}
		
	}
}
