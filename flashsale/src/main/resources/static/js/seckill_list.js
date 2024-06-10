function confirmPhone(id) {
    const itemId = document.getElementById("itemId");
    itemId.value = id;
    const msgBox = document.getElementById("messageBox");
    msgBox.style.display = "block";
    document.body.appendChild(msgBox);
}

function handleCancel() {
    const msgBox = document.getElementById("messageBox");
    msgBox.style.display = "none";
    return false;
}

function beginSeckill() {
    const msgBox = document.getElementById("messageBox");
    const mobile = document.getElementById("mobile").value;
    if (mobile && mobile.length == 10 && !isNaN(mobile)) {
        document.getElementById("theForm").action = document.getElementById("itemId").value;
        msgBox.style.display = "none";
        return true;
    } else {
        alert("請輸入正確的手機號碼！");
        return false;
    }
}

/*
window.onload = function() {
    const now = new Date(); //當前時間
    const endTime = new Date('[[${item.endTime}]]');
    let ns = now.getTime() - endTime.getTime();
    if(ns >= 0) {
        document.getElementById("buyBtn").disabled = true;
        document.getElementById("buyBtn").innerHTML = "秒殺已經結束";
    }
}*/