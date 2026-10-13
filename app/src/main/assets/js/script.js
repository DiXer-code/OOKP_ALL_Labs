// === ЗАГАЛЬНА ЛОГІКА ===
function hideAllLabs() {
    document.querySelectorAll(".lab-text").forEach(el => {
        el.style.display = "none";
    });
}

function showLab(labNumber) {
    hideAllLabs();
    const content = document.getElementById("content-" + labNumber);
    if (content) {
        content.style.display = "block";
    }
}

function openReport(link) {
    if (!link || link.trim() === "" || link === "YOUR_LINK_HERE") {
        alert("Посилання на звіт ще не додано");
        return;
    }
    window.location.href = "report://open?link=" + encodeURIComponent(link);
}

// === LAB 1 (PHYSICS) ===
function updatePhysicsInputs() {
    const type = document.getElementById("phy-type");
    const p1 = document.getElementById("p1");
    const p2 = document.getElementById("p2");
    const p3 = document.getElementById("p3");
    const p4 = document.getElementById("p4");

    if (!type || !p1 || !p2 || !p3 || !p4) return;

    p1.value = "";
    p2.value = "";
    p3.value = "";
    p4.value = "";

    if (type.value === "UNIFORM") {
        p1.placeholder = "Відстань (S)";
        p2.placeholder = "Час (t)";
        p3.style.display = "none";
        p4.style.display = "none";
    } else if (type.value === "ACCELERATED") {
        p1.placeholder = "V0";
        p2.placeholder = "a";
        p3.placeholder = "t";
        p3.style.display = "block";
        p4.style.display = "none";
    } else if (type.value === "DENSITY") {
        p1.placeholder = "m";
        p2.placeholder = "V";
        p3.placeholder = "T";
        p4.placeholder = "P";
        p3.style.display = "block";
        p4.style.display = "block";
    }
}

function calculatePhysics() {
    const type = document.getElementById("phy-type")?.value || "";
    const v1 = document.getElementById("p1")?.value || "";
    const v2 = document.getElementById("p2")?.value || "";
    const v3 = document.getElementById("p3")?.value || "";
    const v4 = document.getElementById("p4")?.value || "";

    if (typeof AndroidPhysics !== "undefined") {
        const res = AndroidPhysics.calculate(type, v1, v2, v3, v4);
        const box = document.getElementById("phy-result");
        if (box) {
            box.style.display = "block";
            box.innerHTML = `Результат: <b>${res}</b>`;
        }
    }
}

// === LAB 2 (DIVISION) ===
function initLab2() {
    const typeSelect = document.getElementById("type");
    if (!typeSelect) return;

    typeSelect.addEventListener("change", function () {
        const a = document.getElementById("a");
        const b = document.getElementById("b");
        if (!a || !b) return;

        if (this.value === "Звичайні дроби") {
            a.placeholder = "1/2";
            b.placeholder = "3/4";
        } else {
            a.placeholder = "Число A";
            b.placeholder = "Число B";
        }
    });
}

function calculate() {
    const a = document.getElementById("a")?.value || "";
    const b = document.getElementById("b")?.value || "";
    const type = document.getElementById("type")?.value || "";

    if (typeof AndroidDivision !== "undefined") {
        const result = AndroidDivision.divide(a, b, type);
        const box = document.getElementById("result");
        if (box) {
            box.style.display = "block";
            box.innerHTML = `Результат: <b>${result}</b>`;
        }
    }
}

// === LAB 3 (ROOFING) ===
function calculateRoof() {
    const roofType = document.getElementById("roof-type")?.value || "";
    const materialType = document.getElementById("material-type")?.value || "";
    const w = document.getElementById("r-width")?.value || "";
    const l = document.getElementById("r-length")?.value || "";
    const h = document.getElementById("r-height")?.value || "";

    if (w === "" || l === "" || h === "") {
        alert("Заповніть всі поля!");
        return;
    }

    if (typeof AndroidRoofing !== "undefined") {
        const result = AndroidRoofing.calculateRoof(roofType, materialType, w, l, h);
        const box = document.getElementById("roof-result");
        if (box) {
            box.style.display = "block";
            if (result.includes("❌")) {
                box.style.background = "#FFEBEE";
                box.style.color = "#C62828";
            } else {
                box.style.background = "#E8F5E9";
                box.style.color = "#2E7D32";
            }
            box.innerHTML = result;
        }
    } else {
        alert("AndroidRoofing не підключено!");
    }
}

// === LAB 4 (NETWORK) ===
function addNote() {
    const input = document.getElementById("noteText");
    if (!input) return;

    const text = input.value;
    if (!text) {
        alert("Будь ласка, введіть текст нотатки!");
        return;
    }

    if (typeof AndroidNetwork !== "undefined") {
        const res = AndroidNetwork.addNote(text);
        input.value = "";
        showNetworkResult(res);
    }
}

function getNotes() {
    if (typeof AndroidNetwork !== "undefined") {
        const res = AndroidNetwork.getNotes();
        showNetworkResult(res);
    }
}

function deleteNote() {
    const input = document.getElementById("deleteIndex");
    if (!input) return;

    const index = input.value;
    if (!index) {
        alert("Будь ласка, введіть номер нотатки для видалення!");
        return;
    }

    if (typeof AndroidNetwork !== "undefined") {
        const res = AndroidNetwork.deleteNote(index);
        input.value = "";
        showNetworkResult(res);
    }
}

function clearNotes() {
    if (typeof AndroidNetwork !== "undefined") {
        const res = AndroidNetwork.clearNotes();
        showNetworkResult(res);
    }
}

function showNetworkResult(text) {
    const box = document.getElementById("network-result");
    if (!box) return;

    box.style.display = "block";
    box.innerHTML = text;
    box.style.textAlign = "left";
}

// === LAB 5 (LOGIC) ===
function solveLab5() {
    const aInput = document.getElementById("logic-a");
    const bInput = document.getElementById("logic-b");
    const cInput = document.getElementById("logic-c");
    const resultDiv = document.getElementById("lab5-result");

    if (!aInput || !bInput || !cInput || !resultDiv) return;

    const aText = (aInput.value || "").trim();
    const bText = (bInput.value || "").trim();
    const cText = (cInput.value || "").trim();

    if (aText === "" || bText === "" || cText === "") {
        showLab5Result("❌ Помилка: введіть коректні дані для всіх трьох осіб.");
        return;
    }

    if (typeof AndroidLogic === "undefined") {
        showLab5Result("❌ Помилка: логічний модуль AndroidLogic не підключено.");
        return;
    }

    const result = AndroidLogic.solve(aText, bText, cText);
    showLab5Result(result);
}

function showLab5Result(text) {
    const resultDiv = document.getElementById("lab5-result");
    if (!resultDiv) return;

    resultDiv.style.display = "block";
    resultDiv.style.whiteSpace = "pre-wrap";
    resultDiv.style.textAlign = "left";
    resultDiv.innerHTML = escapeHtml(text).replace(/\n/g, "<br>");

    if (text.startsWith("❌")) {
        resultDiv.style.background = "#FFEBEE";
        resultDiv.style.color = "#C62828";
    } else if (text.startsWith("⚠️")) {
        resultDiv.style.background = "#FFF8E1";
        resultDiv.style.color = "#EF6C00";
    } else {
        resultDiv.style.background = "#E8F5E9";
        resultDiv.style.color = "#2E7D32";
    }
}

function handleVoiceInputLab5(spokenText) {
    const text = (spokenText || "").trim();
    if (text === "") return;

    const aInput = document.getElementById("logic-a");
    const bInput = document.getElementById("logic-b");
    const cInput = document.getElementById("logic-c");

    const active = document.activeElement;

    if (active && (active.id === "logic-a" || active.id === "logic-b" || active.id === "logic-c")) {
        active.value = text;
    } else if (aInput && aInput.value.trim() === "") {
        aInput.value = text;
    } else if (bInput && bInput.value.trim() === "") {
        bInput.value = text;
    } else if (cInput && cInput.value.trim() === "") {
        cInput.value = text;
    } else if (cInput) {
        cInput.value = text;
    }

    if (typeof AndroidLogic !== "undefined") {
        AndroidLogic.showToast("Голосовий ввід додано у поле");
    }
}

function startVoiceInputLab5() {
    if (typeof AndroidLogic !== "undefined") {
        AndroidLogic.startVoiceInput();
    } else {
        alert("Міст AndroidLogic не підключено!");
    }
}

function escapeHtml(text) {
    return text
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}
// === LAB 6 (COMPARE) ===
function chooseFile(fileNum) {
    if (typeof AndroidCompare !== "undefined") {
        AndroidCompare.openFilePicker(fileNum);
    } else {
        alert("Міст AndroidCompare не підключено!");
    }
}

function setFileName(fileNum, name) {
    const el = document.getElementById("file" + fileNum + "-name");
    if (el) {
        el.innerText = "Обрано: " + name;
    }
}

function startCompare(parallel) {
    const progress = document.getElementById("progress-bar");
    const result = document.getElementById("compare-result");

    if (progress) progress.style.width = "0%";
    if (result) result.style.display = "none";

    if (typeof AndroidCompare !== "undefined") {
        AndroidCompare.startCompare(parallel);
    }
}

function updateProgress(percent) {
    const bar = document.getElementById("progress-bar");
    if (bar) {
        bar.style.width = percent + "%";
    }
}

function showCompareResult(time, matchPercent) {
    const box = document.getElementById("compare-result");
    if (!box) return;

    box.style.display = "block";
    box.innerHTML = `⏱ Час виконання: <b>${time} мс</b><br>📊 Збіг тексту: <b>${matchPercent}%</b>`;
}

function startVoiceInputLab5() {
    if (typeof AndroidLogic !== "undefined") {
        AndroidLogic.startVoiceInput();
    } else {
        alert("Міст AndroidLogic не підключено!");
    }
}

// === INIT ===
document.addEventListener("DOMContentLoaded", function () {
    hideAllLabs();
    initLab2();
});
