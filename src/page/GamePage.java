package page;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cell.Cell;
import cell.Indices;
import cellSociety.CellSociety;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import util.DisplayAlert;

/**
 * The abstract subclass of Page, and super class of all specific pages for simulations.
 * @author Joshua Kopen, Yilin Gao, Harry Liu
 *
 */
public abstract class GamePage extends Page {
	private Group grid;
	private Map<Indices, Cell> cells;
	private int currentStep;
	private Button back;
	private Button start;
	private Button stop;
	private Button step;	
	private Boolean layoutSelected = false;
	private BarChart<Number, String> populationChart;
	private NumberAxis xAxis;
	private CategoryAxis yAxis;
	private List<Color> colorKey;
	private Map<Color, Integer> quantityMap;

	/**
	 * Constructor of the GamePage class. 
	 * @param cs: the CellSociety instance
	 * @param language: a string representing user choice of language
	 * @param p: a Parameters instant from the calling class
	 */
	public GamePage (CellSociety cs, String language, Parameters p) {
		super(cs, language);
		this.setParametersController(p);
		grid = new Group();
		xAxis = new NumberAxis();
		yAxis = new CategoryAxis();
		populationChart = new BarChart<Number,String>(xAxis,yAxis);
		cells = new HashMap<Indices, Cell>();
		back = createButton(getMyResources().getString("BackCommand"), event-> backButton(event));
		start = createButton(getMyResources().getString("StartCommand"), event-> startButton(event));
		stop = createButton(getMyResources().getString("StopCommand"), event-> stopButton(event));
		step = createButton(getMyResources().getString("StepCommand"), event-> stepButton(event));
		quantityMap = new HashMap<Color, Integer>();
	}

	protected Group getGrid(){
		return grid;
	}
	
	/**
	 * The method to return a cell in the grid at a specific location.
	 * @param col: the index of column
	 * @param row: the index of row
	 * @return Cell
	 */
	public Cell getCell(int col, int row){
		return cells.get(new Indices(col, row));
	}
	
	/**
	 * The method to return the current step of the simulation.
	 * @return int
	 */
	public int getCurrentStep () {
		return currentStep;
	}
	
	/**
	 * The method to set the current step.
	 * Called by the game loop in CellSociety.
	 * @param step: current step
	 */
	public void setCurrentStep(int step){
		currentStep = step;
	}		
	
	protected Button getStart(){
		return start;
	}
	
	protected Button getStop(){
		return stop;
	}
	
	protected Button getStep(){
		return step;
	}
	
	protected Button getBack(){
		return back;
	}
	
	/**
	 * The method to get the status of a cell at a given location.
	 * The method will be override by sub classes for different implementations.
	 * @param col: the index of column
	 * @param row: the index of row
	 * @return int: the status of the cell
	 */
	protected int getCellStatus(int col, int row){
		return this.getParametersController().getDefaultStatus();
	}
	
	/**
	 * The method to add a new Cell into the cells object of the Page.
	 * @param col: the index of column
	 * @param row: the index of row
	 * @param c: the Cell instant
	 */
	protected void addCell(int col, int row, Cell c) {
		Indices newKey = new Indices(col, row);
		cells.put(newKey, c);
	}
	
	/**
	 * The method to set the parameter indicating if a type of grid layout is chosen.
	 * @param value
	 */
	protected void setLayoutSelected(boolean value){
		layoutSelected = value;
	}
	
	protected BarChart<Number, String> getChart(){
		return populationChart;
	}
	
	/**
	 * The abstract method to update game information display during each frame.
	 * The method will be implemented by each sub class.
	 */
	public abstract void updateTextInfo();
	/**
	 * Creates Map which tracks the color quantities 
	 * @param newValue
	 */
	public void quantityMap () {
		for (Color color: this.getParametersController().getColorSet()){
			quantityMap.put(color, 0);
		}
	}
	/**
	 * Updates the color and the display of the Bar Graph on each step.
	 */
	public void updateColorandData () {
		int i, j;
		for (i = 0; i < this.getParametersController().getRow(); i++) {
			for (j = 0; j < this.getParametersController().getCol(); j++) {
				Color color = this.getParametersController().getColor(getCell(i,j).getStatus());
				getCell(i,j).changeColor(this.getParametersController().getColor(getCell(i,j).getStatus()));
				this.quantityMap.put(color, this.quantityMap.get(color)+1);	
			}	
		}
		updateChartDisplay();
		quantityMap.replaceAll((k,v) -> 0);;
	}
	public void updateChartDisplay(){		
		for (Series<Number, String> series : populationChart.getData()) {
			for (int x = 0; x<series.getData().size(); x++) {
				XYChart.Data<Number, String> data = series.getData().get(x);
				Node node = data.getNode();
				node.setStyle("-fx-bar-fill:" + "#" + colorKey.get(x).toString().substring(2));
				data.setXValue(quantityMap.get(colorKey.get(x)));
			}
		}
	}
	public void createPopulationChart(){
		xAxis.setLabel("Quantity"); 
		yAxis.setLabel("Status");
		XYChart.Series<Number, String> populationSeries = new Series<Number, String>();
		colorKey = new ArrayList<Color>(quantityMap.keySet());
		
		for (int x = 0; x<quantityMap.keySet().size(); x++){
			String status = "Status " + this.getParametersController().getColorStatus(colorKey.get(x));
			Number quantity = quantityMap.get(colorKey.get(x));
			populationSeries.getData().add(new Data<Number, String>(quantity, status));
		}
		populationChart.getData().clear();
		populationChart.getData().add(populationSeries);
		populationChart.setLegendVisible(false);
	}
	/**
	 * The handler of the "BACK" button.
	 * When the button is pressed, the game will return to the splash screen.
	 * @param event
	 */
	private void backButton(ActionEvent event) {
		this.getCellSociety().stopGameLoop();
		this.getCellSociety().loadPage("Welcome");
	}
	/**
	 * The handler of the "START" button.
	 * When the button is pressed, the simulation will run consecutively.
	 * @param event
	 */
	private void startButton(ActionEvent event) {
		if (layoutSelected){
			this.getCellSociety().setIsStep(false);
			this.getCellSociety().beginGameLoop();
		}
		else{
			DisplayAlert.displayAlert(getMyResources().getString("SelectCommand"));
		}
	}
	/**
	 * The handler of the "STOP" button.
	 * When the button is pressed, the simulation will stop. 
	 * Only if the "START" or the "STEP" button is pressed, the simulation will resume.
	 * @param event
	 */
	private void stopButton(ActionEvent event) {
		this.getCellSociety().stopGameLoop();
	}
	/**
	 * The handler of the "STEP" button.
	 * When the button is pressed, the simulation will perform the next step.
	 * @param event
	 */
	private void stepButton(ActionEvent event){
		if (!layoutSelected){
			DisplayAlert.displayAlert(getMyResources().getString("SelectCommand"));
		}	
		else{
			this.getCellSociety().setIsStep(true);
			this.getCellSociety().setNextStep(true);
			this.getCellSociety().beginGameLoop();
		}	
	}
}