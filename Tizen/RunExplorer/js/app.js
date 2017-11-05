(function(){
	var myapp = {
			// constatns
			SECOND_TO_MILISECOND: 1000,
			
			// preference keys
	        PREFERENCE_KEY_IS_RUNNING: 'isRunning',
	        PREFERENCE_KEY_SPEED: 'speed',
	        PREFERENCE_KEY_LOCATION: 'location',
	        PREFERENCE_KEY_HEARTRATE: 'heartrate',
			
	        // connection page
	        CONNECTION_PAGE: 'connection-page',
	        
			// main page
			MAIN_PAGE: 'main-page',
			MAIN_SPEED: 'content-speed',
			MAIN_LOCATION: 'content-location',
			MAIN_HEARTRATE: 'content-heartrate',
			MAIN_ERROR: 'content-error',
			MAIN_STOPWATCH: 'content-stopwatch',
			MAIN_DISTANCE: 'content-distance',
			
			// map page
			MAP_PAGE: 'map-page',
			MAP_DIV: 'map',
			
			// controls page
			CONTROLS_PAGE: 'controls-page',
			CONTROLS_PAUSE_BUTTON: 'pause-button',
			CONTROLS_STOP_BUTTON: 'stop-button',
			CONTROLS_START_BUTTON: 'start-button',
			
			// connection objects
			connection: {
				SAAgent: {},
			    SASocket: {},
			    connectionListener: {},
			},
	
			// used data	
			data: {
				main: {
					speed: 0,
					location: {},
					distance: 0,
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
					distance: {},
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
			},
			
			map: {
				ready: false,
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
    	
    	initConnection.call(myapp);
        initUI.call(myapp);
    	initSensors.bind(myapp, initMap.bind(myapp))();
    	initControls.call(myapp);
    	
    	window.addEventListener('rotarydetent', myapp.navigation.rotarydetentHandler);
        window.addEventListener('tizenhwkey', function (ev) {
            if (ev.keyName === "back") {
            	console.log(myapp.connection.SAAgent, myapp.connection.SASocket, myapp.connection.connectionListener);
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