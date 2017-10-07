(function(){
	var myapp = {
			// constatns
			SECOND_TO_MILISECOND: 1000,
			
			// preference keys
	        PREFERENCE_KEY_IS_RUNNING: 'isRunning',
	        PREFERENCE_KEY_SPEED: 'speed',
	        PREFERENCE_KEY_LOCATION: 'location',
	        PREFERENCE_KEY_HEARTRATE: 'heartrate',
			
			// main page
			MAIN_PAGE: 'main-page',
			MAIN_SPEED: 'content-speed',
			MAIN_LOCATION: 'content-location',
			MAIN_HEARTRATE: 'content-heartrate',
			MAIN_ERROR: 'content-error',
			MAIN_STOPWATCH: 'content-stopwatch',
			
			// map page
			MAP_PAGE: 'map-page',
			MAP_DIV: 'map',
			
			// controls page
			CONTROLS_PAGE: 'controls-page',
			CONTROLS_PAUSE_BUTTON: 'pause-button',
			CONTROLS_STOP_BUTTON: 'stop-button',
			CONTROLS_START_BUTTON: 'start-button',
				
			// used data	
			data: {
				main: {
					speed: 0,
					location: {},
					heartRate: 0,
					sampleInterval: 1000,
					callbackInterval: 1000,
					heartCallbackInterval: 3000,
					gpsMaxAge: 3000
				},
				map:{
					myMap: {},
					currentPositionMarker: {},
				}
			},
			
			// page navigation
			navigation: {
				rotarydetentHandler: {}
			},
			
			// ui elements
			ui: {
				mainpage: {
					speed: {},
					location: {},
					heartRate: {},
					stopWatch: {},
					error: {},
				},
				mappage: {
					map: {}
				},
				controlspage: {
					stopButton: {},
					startButton: {},
					pauseButton: {}
				}
			},
			
			sensors: {
				start: {},
				pause: {},
				stop: {},
				
				// stopwatch
				StopWatch: {
					set: {},
					onChange: {},
					exit: {},
					watch: {},
					time: 0
				},
				
				// speed listener
				SpeedChangeListener: {
					set: {},
					onChange: {},
					exit: {}
				},
				
				// geolocation listener
				GeolocationChangeListener: {
					watch: {},
					set: {},
					onChange: {},
					exit: {}
				},
				
				HeartRateChangeListener: {
					set: {},
					onChange: {},
					exit: {}
				}
			},
			
			map: {
				init: {},
				resize: {},
				updatePosition: {},
				reset: {}
			},
			
			// on error and exit
			onerror: {},
			exit: {}
	};
	
	/**
     *  Sets a function for window.onload .
     */
    window.onload = function () {
    	console.log('Load start');
        
        initUI.call(myapp);
    	initMap.call(myapp);
    	initSensors.call(myapp);
    	initControls.call(myapp);
    	
    	myapp.map.init();
    	
    	window.addEventListener('rotarydetent', myapp.navigation.rotarydetentHandler);
        window.addEventListener('tizenhwkey', function (ev) {
            if (ev.keyName === "back") {
            	
	    		if(checkPage(myapp.MAIN_PAGE)){
	    			myapp.exit();
	    		}else{
	    			window.history.back();
	    		}
            }
        });
    	
        tizen.preference.setValue(myapp.PREFERENCE_KEY_IS_RUNNING, true);
    };
})();