var initUI = function() {
	var self = this;
	
	/**
	 * Setting 'isRunning' as false and exits the app.
	 */
	self.exit = function() {
		try {
			self.sensors.stop();
			tizen.preference.setValue(self.PREFERENCE_KEY_IS_RUNNING, false);
			console.log('Everything turned off');
			tizen.application.getCurrentApplication().exit();
		}catch (ignore) {}
	};
	
	// Init all ui elements
	(function(){
		self.ui.mainpage.speed = document.getElementById(self.MAIN_SPEED);
		self.ui.mainpage.location = document.getElementById(self.MAIN_LOCATION);
		self.ui.mainpage.heartRate = document.getElementById(self.MAIN_HEARTRATE);
		self.ui.mainpage.error = document.getElementById(self.MAIN_ERROR);
		self.ui.mainpage.stopwatch = document.getElementById(self.MAIN_STOPWATCH);
		
		self.ui.mappage.map = document.getElementById(self.MAP_DIV);
		
		self.ui.controlspage.stopButton = document.getElementById(self.CONTROLS_STOP_BUTTON);
		self.ui.controlspage.startButton = document.getElementById(self.CONTROLS_START_BUTTON);
		self.ui.controlspage.pauseButton = document.getElementById(self.CONTROLS_PAUSE_BUTTON);
	})();
	
	/**
	 * Sets on error action which shows error's name
	 * @param {Tizen.WebAPIError} err - error to handle
	 */
	self.onerror = function(err) {
		self.ui.mainpage.error.innerHTML = err.name;
	};
	
	
	self.navigation.rotarydetentHandler = function(e) {
		var pages = [self.CONTROLS_PAGE, self.MAIN_PAGE, self.MAP_PAGE];
		var change = false;
		var currentIndex = pages.findIndex(function(name){
												return checkPage(name);
											});
		
		if(e.detail.direction === 'CW'){
			if(currentIndex !== pages.length-1){
				tau.changePage("#" + pages[currentIndex+1]);
				change=true;
			}
		}else if(currentIndex !== 0){
			tau.changePage("#" + pages[currentIndex-1]);
			change=true;
		}
		
		if(currentIndex === pages.indexOf(self.MAP_PAGE) && change){
			self.map.init();
		}
	};
	console.log('UI set');
};



/**
 * Checks if given page is active and uses right callback
 */
var checkPage = function(name){
	var page = document.getElementsByClassName('ui-page-active')[0],
		pageid = page ? page.id : "";
	return pageid === name;
};