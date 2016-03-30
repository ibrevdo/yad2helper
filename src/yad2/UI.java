package yad2;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Comparator;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
 
public class UI extends Application {
	
	private static Yad2Helper 	yad2help;    
	private TableView<AdInfo> 	table;
	private BorderPane 			root ;
	
	public UI()
	{
		table = new TableView<>();
		try {
			yad2help = new Yad2Helper();
		} catch (IOException | ParseException e) {
			e.printStackTrace();
			System.exit(0);
		}		
	}
	
	private void buildTable()
	{
		ObservableList<AdInfo> data;
		data = FXCollections.observableArrayList(yad2help.getWatchedAds());
		FXCollections.sort(data);
		
	  	
		TableColumn<AdInfo,Boolean> isWatchedCol 		= new TableColumn<>("X");
		TableColumn<AdInfo,Hyperlink> linkCol 			= new TableColumn<>("Link");
		TableColumn<AdInfo,String> isNewCol 				= new TableColumn<>("New");
		TableColumn<AdInfo,String> publishDateCol 		= new TableColumn<>("Publish date");
    	TableColumn<AdInfo,String> updateDateCol 		= new TableColumn<>("Update date");
        TableColumn<AdInfo,String> notesCol 				= new TableColumn<>("Notes");
        
        isWatchedCol.setSortable(false);
        linkCol.setSortable(false);
        isNewCol.setSortable(false);
        publishDateCol.setSortable(false);
        updateDateCol.setSortable(false);
        notesCol.setSortable(false);
        
        
        
        isWatchedCol.setCellValueFactory( new Callback<TableColumn.CellDataFeatures<AdInfo,Boolean>, ObservableValue<Boolean>>() {			
			@Override
			public ObservableValue<Boolean> call(CellDataFeatures<AdInfo, Boolean> param) {
				return param.getValue().getIsWatched();
			}
		});
        isWatchedCol.setCellFactory(CheckBoxTableCell.forTableColumn(isWatchedCol));
                
        linkCol.setCellValueFactory(new PropertyValueFactory<AdInfo,Hyperlink>("Link"));
        linkCol.setCellFactory(new Callback<TableColumn<AdInfo,Hyperlink>,TableCell<AdInfo,Hyperlink>>(){
			@Override
			public TableCell<AdInfo, Hyperlink> call(TableColumn<AdInfo, Hyperlink> param) {
				TableCell<AdInfo, Hyperlink> cell = new TableCell<AdInfo, Hyperlink>() {
                    @Override
                    public void updateItem(final Hyperlink item, boolean empty) {
                    	super.updateItem(item, empty);
                    	setGraphic(item);
                    	if (item != null) {
                    		item.setOnAction(new EventHandler<ActionEvent>() {
                        	    @Override
                        	    public void handle(ActionEvent e) {
                        	    	getHostServices().showDocument(item.getText());
                        	    }
                        	});
                    	}
                    }
				};		
				return cell;
			}});
        

        isNewCol.setCellFactory(new Callback<TableColumn<AdInfo,String>, TableCell<AdInfo,String>>() {
			
			@Override
			public TableCell<AdInfo, String> call(TableColumn<AdInfo, String> param) {
				return new TableCell<AdInfo, String>()
						{
							@Override
							protected void updateItem(String item, boolean empty) {
								super.updateItem(item, empty);								
								if(!empty){
									 if (item.equals("true")) setText("new");
									 else setText("");
								 }								 								
							}
						};
			}
		});
        isNewCol.setCellValueFactory(new PropertyValueFactory<AdInfo,String>("IsNew"));
          
                
        publishDateCol.setCellValueFactory(new PropertyValueFactory<AdInfo,String>("PublishDate"));
        updateDateCol.setCellValueFactory(new PropertyValueFactory<AdInfo,String>("UpdateDate"));
        
        notesCol.setCellValueFactory(new PropertyValueFactory<AdInfo,String>("Notes"));
        notesCol.setCellFactory(TextFieldTableCell.<AdInfo>forTableColumn());
        notesCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<AdInfo,String>>() {
			@Override
			public void handle(CellEditEvent<AdInfo, String> arg0) {
				arg0.getTableView().getItems().get(arg0.getTablePosition().getRow()).setNotes(arg0.getNewValue());				
			}
		});
        notesCol.setPrefWidth(300);
        
    	table.setEditable(true);
    	table.setItems(data);
    	table.getColumns().addAll(isWatchedCol,linkCol,isNewCol,updateDateCol,publishDateCol,notesCol);  
    	
    	/*
    	updateDateCol.setSortable(true);
    	updateDateCol.setSortType(SortType.DESCENDING);      
    	table.getSortOrder().add(updateDateCol);
    	updateDateCol.setSortable(false);
    	*/
    	//table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	}
	
	private void refreshTable()
	{
		ObservableList<AdInfo> data = FXCollections.observableArrayList(yad2help.getWatchedAds());
		FXCollections.sort(data);
		table.setItems(data);		
		table.refresh();
	}
	
	@Override
    public void start(Stage primaryStage) throws ParseException {
		
		Button openHtmlDirButton = new Button("Load HTML");
		openHtmlDirButton.setOnAction(new EventHandler<ActionEvent>() {			
			@Override
			public void handle(ActionEvent arg0) {
				
				DirectoryChooser dirChooser = new DirectoryChooser();
				dirChooser.setTitle("Choose folder containg HTML files");
				File selectedDir = dirChooser.showDialog(null);
				if (selectedDir != null) {
				    System.out.println("File selected: " + selectedDir.getName());
				    
				    try {
						yad2help.onFreshLinks(selectedDir);
						table = new TableView<>();
						buildTable();
						root.setCenter(table);
						
					} catch (IOException | ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Platform.exit();
					}
				}
				else {
					System.out.println("File selection cancelled.");
				}				
			}
		});
		
		
		Button updateTableButton = new Button("Update");
		updateTableButton.setOnAction(new EventHandler<ActionEvent>() {			
			@Override
			public void handle(ActionEvent arg0) {
				refreshTable();
			}
		});
		
		ToolBar toolBar = new ToolBar();
		toolBar.getItems().addAll(openHtmlDirButton, updateTableButton);
	  	
		
		///////// TABLE
		buildTable();
	    	
        root = new BorderPane();
        
        root.setTop(toolBar);
        root.setCenter(table);
        
        primaryStage.setTitle("Yad 2 Helper");
        primaryStage.setScene(new Scene(root, 1300, 800));
        //primaryStage.setScene(new Scene(root));
        
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				refreshTable();
				yad2help.updateNewAndWatched(table.getItems());
				try {
					yad2help.saveDbToDisk();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}        	
		});
        primaryStage.show();
    }
		
	   public static void main(String[] args) {
	        launch(args);
	    }
}