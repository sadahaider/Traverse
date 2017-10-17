var track = document.getElementById('current_track');

var playButton = document.getElementById('playButton');
var muteButton = document.getElementById('muteButton');

var length = document.getElementById('fullDuration');
var lengthInSeconds = 0;
var currentTime = document.getElementById('currentTime');

track.addEventListener('loadedmetadata',
    function() {
        lengthInSeconds = track.duration;
        var minutes = parseInt(track.duration/60);
        var seconds = parseInt(track.duration%60);
        length.innerHTML = minutes + ":" + seconds;
    },
    false );

var barSize = 640;
var bar = document.getElementById('defaultBar');
var progressBar = document.getElementById('progressBar');

playButton.addEventListener('click', playOrPause, false);
muteButton.addEventListener('click', muteOrUnmute, false);
bar.addEventListener('click', clickedBar, false);

function playOrPause() {
    console.log("pop");
    if(!track.paused && !track.ended) {
        track.pause();
        playButton.style.backgroundImage = 'url(\'https://i.imgur.com/MM40XBm.png\')';
        window.clearInterval(updateTime);
    } else {
        track.play();
        playButton.style.backgroundImage = 'url(\'https://i.imgur.com/1fKTLk2.png\')';
        updateTime = setInterval(update, 500);
    }
}

function muteOrUnmute() {
    console.log("mom");
    if(track.muted == true) {
        track.muted = false;
        muteButton.style.backgroundImage = 'url(\'https://i.imgur.com/uMzAZfa.png\')';
    } else {
        track.muted = true;
        muteButton.style.backgroundImage = 'url(\'https://i.imgur.com/pRG2M4c.png\')';
    }
}

function update() {
    if(!track.ended) {
        var playedMinutes = parseInt(track.currentTime/60);
        var playedSeconds = parseInt(track.currentTime%60);
        if(playedSeconds < 10) {
            currentTime.innerHTML = playedMinutes + ':0' + playedSeconds;
        } else {
            currentTime.innerHTML = playedMinutes + ':' + playedSeconds;
        }
        var size = parseInt(track.currentTime*barSize/lengthInSeconds);
        progressBar.style.width = size + "px";
    } else {
        currentTime.innerHTML = "0:00";
        playButton.style.backgroundImage = 'url(\'https://i.imgur.com/MM40XBm.png\')';
        progressBar.style.width = "0px";
        window.clearInterval(updateTime);
    }
}

function clickedBar(event) {
    if(!track.ended) {
        var mouseX = event.pageX - bar.offsetLeft;
        console.log(mouseX);
        var newTime = mouseX * lengthInSeconds/barSize;
        track.currentTime = newTime;
        progressBar.style.width = mouseX + 'px';
    }
}