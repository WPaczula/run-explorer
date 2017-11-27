var initControls = function(){
	var self = this;
	
	function pause(){
		self.sensors.pause();
		self.ui.controlspage.pauseButton.className = "invisible"
		self.ui.controlspage.startButton.className = "play-btn"
	}
	
	function start(){
		self.sensors.start();
		tau.changePage("#" + self.MAIN_PAGE);
		self.ui.controlspage.pauseButton.className = "play-btn"
		self.ui.controlspage.startButton.className = "invisible"
	}
	
	function stop(){
		self.sensors.stop();
		if(self.map.ready)
			self.map.reset();
	}
	
	self.controls.start = start;
	self.controls.stop = stop;
	self.controls.pause = pause;
	self.ui.controlspage.startButton.onclick = start;
	self.ui.controlspage.pauseButton.onclick = pause;
};