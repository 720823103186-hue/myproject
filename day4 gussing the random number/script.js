let randomNumber = Math.floor(Math.random() * 20) + 1;
let attempts = 0;

function checkGuess() {
    const guess = Number(document.getElementById("guessInput").value);
    const message = document.getElementById("message");
    const attemptsText = document.getElementById("attempts");

    if (!guess) {
        message.textContent = "Please enter a number!";
        return;
    }

    attempts++;

    if (guess === randomNumber) {
        message.textContent = `Correct! 🎉 The number was ${randomNumber}`;
        message.style.color = "#00ffcc";
    } 
    else if (guess < randomNumber) {
        message.textContent = "Too low! 📉 Try again.";
        message.style.color = "yellow";
    } 
    else {
        message.textContent = "Too high! 📈 Try again.";
        message.style.color = "orange";
    }

    attemptsText.textContent = `Attempts: ${attempts}`;
}

function resetGame() {
    randomNumber = Math.floor(Math.random() * 100) + 1;
    attempts = 0;

    document.getElementById("guessInput").value = "";
    document.getElementById("message").textContent = "";
    document.getElementById("attempts").textContent = "Attempts: 0";
}