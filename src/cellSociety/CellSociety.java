package cellSociety;
import java.io.File;
import java.util.Hashtable;

import animation.Animation;
import animation.AnimationGameOfLife;
import animation.AnimationPredator;
import animation.AnimationSegregation;
import animation.AnimationSlime;
import animation.AnimationSpreadingOfFire;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.util.Duration;
import page.GamePage;
import page.Page;
import page.PageGameOfLife;
import page.PagePredator;
import page.PageSegregation;
import page.PageSlime;
import page.PageSpreadingOfFire;
import page.Parameters;
import page.WelcomePage;

/**
 * The class to maintain pages, animations and the game loop.
 * @author Yilin Gao
 *
 */
public class CellSociety {
	private Stage stage;
	private File inputFile;
	File configurationFile = new File(getClass().getClassLoader().getResource("OverallConfiguration.xml").getFile());
	private Hashtable<String, Page> pages = new Hashtable<>();
	private Hashtable<String, Animation> animations = new Hashtable<>();
	private String currentType = "";
	private String nextType = "";
	
	private Timeline timeline; 
	private double millisecondDelay;
	
	// if the simulation is step by step or consecutive.
	private Boolean isStep = false; 
	private Boolean nextStep = false;
		
	public Stage getStage(){
		return stage;
	}
	
	public File getInputFile(){
		return inputFile;
	}
	
	public File getConfigFile(){
		return configurationFile;
	}
	
	public Page getPage(String type){
		return pages.get(type);
	}
	
	public Animation getAnimation(String type){
		return animations.get(type);
	}
	
	public String getCurrentType(){
		return currentType;
	}
	
	public String getNextType(){
		return nextType;
	}
	
	public void setInputFile(File file){
		inputFile = file;
	}
	
	public void setCurrrentType(String s){
		currentType = s;
	}
	
	public void setNextType(String s){
		nextType = s;
	}
	
	public void setDelay(double step){
		millisecondDelay = 1000 / step;
	}
	
	public void setIsStep(Boolean value){
		isStep = value;
	}
	
	public void setNextStep(Boolean value){
		nextStep = value;
	}
	
	/**
	 * The constructor of the CellSociety class.
	 * The constructor always loads the splash page.
	 * @param theStage
	 */
	public CellSociety(Stage theStage) {
		stage = theStage;
		initializePage("Welcome");
	}
	
	/**
	 * A method to initialize a page and an animation of a given type.
	 * @param type: the String to represent simulation type.
	 *  TODO how to refactor
	 */
	public void initializePage(String type){
		currentType = type;
		if (type.equals("Welcome")){
			Page newWelcomePage = new WelcomePage(this, "English");
			pages.put(type, newWelcomePage);
			newWelcomePage.showPage();
		}
		else if (type.equals("Game of Life")){
			String language = pages.get("Welcome").getLanguage();
			Parameters parametersController = pages.get("Welcome").getParametersController();
			Page newGameOfLifePage = new PageGameOfLife(this, language, parametersController);
			pages.put(type, newGameOfLifePage);
			Animation newGameOfLifeAnimation = new AnimationGameOfLife(this, parametersController);
			animations.put(type, newGameOfLifeAnimation);
		}
		else if (type.equals("Segregation")){
			String language = pages.get("Welcome").getLanguage();
			Parameters parametersController = pages.get("Welcome").getParametersController();
			Page newSegregationPage = new PageSegregation(this, language, parametersController);
			pages.put(type, newSegregationPage);
			Animation newSegregationAnimation = new AnimationSegregation(this, parametersController); 
			animations.put(type, newSegregationAnimation);
		}
		else if (type.equals("Fire")){
			String language = pages.get("Welcome").getLanguage();
			Parameters parametersController = pages.get("Welcome").getParametersController();
			Page newFirePage = new PageSpreadingOfFire(this, language, parametersController);
			pages.put(type, newFirePage);
			Animation newFireAnimation = new AnimationSpreadingOfFire(this, parametersController);
			animations.put(type, newFireAnimation);
		}
		else if (type.equals("Predator")){
			String language = pages.get("Welcome").getLanguage();
			Parameters parametersController = pages.get("Welcome").getParametersController();
			Page newPredatorPage = new PagePredator(this, language, parametersController);
			pages.put(type, newPredatorPage);
			Animation newPredatorAnimation = new AnimationPredator(this, parametersController);
			animations.put(type, newPredatorAnimation);
		}
		else if (type.equals("Slime")) {
			String language = pages.get("Welcome").getLanguage();
			Parameters parametersController = pages.get("Welcome").getParametersController();
			Page newSlimePage = new PageSlime(this, language, parametersController);
			pages.put(type, newSlimePage);
			Animation newSlimeAnimation = new AnimationSlime(this, parametersController);
			animations.put(type, newSlimeAnimation);
		}
	}
	
	/**
	 * A method to load a new page from other pages.
	 * @param type: the String to represent simulation type.
	 */
	public void loadPage(String type){
		pages.clear();
		animations.clear();
		inputFile = null;
		nextType = "";
		initializePage(type);
	}
	
	/**
	 * The method to set up the game loop (with JavaFX Timeline)
	 */
	public void setupGameLoop(){
		Duration oneFrameDuration = Duration.millis(millisecondDelay);
		KeyFrame oneFrame = new KeyFrame(oneFrameDuration, new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent event) {
				actionsPerFrame();
			}
			
		});
		timeline = new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.getKeyFrames().add(oneFrame);
	}
	
	/**
	 * The method to start the game loop.
	 */
	public void beginGameLoop(){
		timeline.play();
	}
	
	/**
	 * The method to stop the game loop.
	 */
	public void stopGameLoop(){
		timeline.stop();
	}
	
	private void actionsPerFrame() {
		// if the current mode is consecutive simulation
		if (!isStep){
			detailedActions();
		}
		// if the current mode is simulation step by step
		else {
			if (nextStep){
				detailedActions();
				nextStep = false;
			}
		}
	}

	private void detailedActions() {
		animations.get(currentType).calculateMove();
		((GamePage)pages.get(currentType)).setCurrentStep(((GamePage)pages.get(currentType)).getCurrentStep() + 1);
		((GamePage) pages.get(currentType)).updateColorandData();
		((GamePage) pages.get(currentType)).updateTextInfo();
	}
}