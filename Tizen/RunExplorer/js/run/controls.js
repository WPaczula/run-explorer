var initControls = function(){
	var self = this;
	
	function pause(){
		self.sensors.pause();
	}
	
	function start(){
		self.sensors.start();
		tau.changePage("#" + self.MAIN_PAGE);
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