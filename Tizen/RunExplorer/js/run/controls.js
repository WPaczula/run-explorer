var initControls = function(){
	var self = this;
	
	function hideButton(button){
		button.className = 'invisible';
	}
	
	function showButton(button){
		button.className = '';
	}
	
	function pause(){
		self.sensors.pause();
		hideButton(self.ui.controlspage.pauseButton);
		showButton(self.ui.controlspage.startButton);
	}
	
	function start(){
		self.sensors.start();
		hideButton(self.ui.controlspage.startButton);
		showButton(self.ui.controlspage.pauseButton);
		tau.changePage("#" + self.MAIN_PAGE);
	}
	
	function stop(){
		self.sensors.stop();
		if(self.map.ready)
			self.map.reset();
		hideButton(self.ui.controlspage.pauseButton);
		showButton(self.ui.controlspage.startButton);
	}
	
	self.ui.controlspage.stopButton.onclick = stop;
	self.ui.controlspage.startButton.onclick = start;
	self.ui.controlspage.pauseButton.onclick = pause;
};