function countDown(startTime, endTime) {
    const now = new Date(); // 當前時間
    let ns = new Date(endTime).getTime() - now.getTime(); // 毫秒差
    let timer = null;
    const remainingTime = function () {
        if (ns > 1000) {
            ns -= 1000;

            // 相差天數
            let day = Math.floor(ns / (24 * 3600 * 1000));

            // 小時數
            let remainingMills1 = ns % (24 * 3600 * 1000); // 計算天數後面剩餘的毫秒數
            let hour = Math.floor(remainingMills1 / (3600 * 1000));

            // 分鐘數
            let remainingMills2 = remainingMills1 % (3600 * 1000); // 計算小時後面剩餘的毫秒數
            let minutes = Math.floor(remainingMills2 / (60 * 1000));

            // 秒數
            let remainingMills3 = remainingMills2 % (60 * 1000); // 計算分鐘後面剩餘的毫秒數
            let second = Math.floor(remainingMills3 / 1000);
            return "距離秒殺结束：" + day + "天 " + hour + "時 " + minutes + "分 " + second + "秒";
        } else {
            document.getElementById("seckillBtn").disabled = true;
            document.getElementById("seckillBtn").style.display = "none";

            return "秒殺已經結束";

            // 停止定時器
            if (timer !== null)
                clearInterval(timer);
        }
    }

    if (ns > 0) {
        document.getElementById("countDown").innerHTML = remainingTime();
        const timer = setInterval(function () {
            document.getElementById("countDown").innerHTML = remainingTime();
        }, 1000)
    } else {
        document.getElementById("seckillBtn").disabled = true;
        document.getElementById("seckillBtn").style.display = "none";
        document.getElementById("countDown").innerHTML = "秒杀已经结束";
    }
}
