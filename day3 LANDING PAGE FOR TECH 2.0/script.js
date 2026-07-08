// Welcome message
window.onload = function () {
    alert("Welcome to TechLearn!");
};

// Get Started button
const btn = document.querySelector(".btn");

if (btn) {
    btn.addEventListener("click", function () {
        alert("Redirecting to Courses Page...");
    });
}

// Course card hover effect
const cards = document.querySelectorAll(".card, .course-card");

cards.forEach(function(card) {

    card.addEventListener("mouseenter", function() {
        card.style.transform = "scale(1.05)";
        card.style.transition = "0.3s";
    });

    card.addEventListener("mouseleave", function() {
        card.style.transform = "scale(1)";
    });

    card.addEventListener("click", function() {
        alert("You selected: " + card.querySelector("h2, h3").innerText);
    });

});

// Smooth scrolling for navigation links
document.querySelectorAll('a[href^="#"]').forEach(function(anchor) {
    anchor.addEventListener("click", function(e) {
        e.preventDefault();

        const target = document.querySelector(this.getAttribute("href"));

        if (target) {
            target.scrollIntoView({
                behavior: "smooth"
            });
        }
    });
});