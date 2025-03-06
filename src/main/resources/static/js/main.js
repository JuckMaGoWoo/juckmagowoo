let mediaRecorder;
let audioChunks = [];
let isRecording = false;
let isWaitingResponse = false; // 응답 대기 상태
let isListening = false; // 음성 실행 상태

document.addEventListener("DOMContentLoaded", () => {
    const button = document.getElementById("recordBtn");
    const buttonText = document.getElementById("buttonText");
    const listenVideo = document.getElementById("video1");
    const speakVideo = document.getElementById("video2");

    if (!button || !buttonText) {
        console.error("버튼 또는 버튼 텍스트 요소를 찾을 수 없습니다.");
        return;
    }

    button.addEventListener("click", async () => {
        if (isWaitingResponse || isListening) return; // "생각 중" 또는 "듣는 중" 상태에서는 클릭 방지

        if (!isRecording) {
            try {
                const stream = await navigator.mediaDevices.getUserMedia({ audio: true });

                mediaRecorder = new MediaRecorder(stream);
                mediaRecorder.start();
                isRecording = true;

                // 🔴 녹음 중 상태
                button.classList.add("active");
                buttonText.textContent = "멈추기";

                mediaRecorder.ondataavailable = event => {
                    audioChunks.push(event.data);
                };

                console.log("🎙️ 녹음 시작됨...");
            } catch (error) {
                console.error("❌ 마이크 접근 오류:", error);
                alert("마이크에 접근할 수 없습니다. 브라우저 설정을 확인하세요.");
            }

        } else {
            mediaRecorder.stop();
            isRecording = false;

            // 🟦 "생각중..." 상태 (파란색)
            button.classList.remove("active");
            button.classList.add("thinking");
            buttonText.textContent = "생각중입니다";
            isWaitingResponse = true;

            // API 요청 전송
            mediaRecorder.onstop = async () => {
                const audioBlob = new Blob(audioChunks, { type: "audio/wav" });
                audioChunks = [];

                const formData = new FormData();
                formData.append("file", audioBlob, "recording.wav");

                try {
                    const queryString = window.location.search;
                    const urlParams = new URLSearchParams(queryString);
                    const userId = urlParams.get("userId");

                    const response = await fetch(`/api/audio/stt?userId=${userId}`, {
                        method: "POST",
                        body: formData,
                    });

                    if (response.ok) {
                        const audioBlob = await response.blob();
                        const audioUrl = URL.createObjectURL(audioBlob);
                        const audio = new Audio(audioUrl);

                        // 🟪 "듣는 중" 상태 (보라색, 애니메이션 추가)
                        button.classList.remove("thinking");
                        button.classList.add("listening");
                        buttonText.textContent = "듣는 중...";
                        isWaitingResponse = false;
                        isListening = true;

                        audio.oncanplaythrough = () => {
                            audio.play().catch(error => console.error("🔇 자동 재생 실패:", error));
                        };

                        audio.addEventListener("ended", () => {
                            console.log("🎬 오디오 종료, 영상 변경");
                            speakVideo.style.opacity = 0;
                            listenVideo.style.opacity = 1;

                            // ✅ 버튼 원래 상태로 복구
                            button.classList.remove("listening");
                            buttonText.textContent = "말하기";
                            isListening = false;
                        });

                        console.log("🔊 오디오 자동 재생 시작!");
                    } else {
                        console.error("⚠️ 오디오 업로드 실패:", response.statusText);
                    }
                } catch (error) {
                    console.error("⚠️ API 요청 중 오류 발생:", error);
                }
            };

            console.log("🛑 녹음 종료 & API 전송");
        }
    });
});





