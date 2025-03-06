let mediaRecorder;
let audioChunks = [];
let isRecording = false;

document.addEventListener("DOMContentLoaded", () => {
    const button = document.getElementById("recordBtn");

    button.addEventListener("click", async () => {
        if (!isRecording) {
            try {
                // 🎤 마이크 접근 요청
                const stream = await navigator.mediaDevices.getUserMedia({ audio: true });

                // 🟢 MediaRecorder 생성 및 녹음 시작
                mediaRecorder = new MediaRecorder(stream);
                mediaRecorder.start();
                isRecording = true;
                button.style.filter = "brightness(50%)"; // ✅ 버튼 밝기 50%로 낮춤

                // 🔴 녹음 데이터 저장
                mediaRecorder.ondataavailable = event => {
                    audioChunks.push(event.data);
                };

                mediaRecorder.onstop = async () => {
                    // 🎵 Blob을 사용하여 오디오 데이터 생성
                    const audioBlob = new Blob(audioChunks, { type: "audio/wav" });
                    audioChunks = [];

                    // 📤 FormData에 오디오 파일 추가
                    const formData = new FormData();
                    formData.append("file", audioBlob, "recording.wav");

                    try {
                        const queryString = window.location.search;
                        const urlParams = new URLSearchParams(queryString);
                        const userId =urlParams.get("userId");
                        // 🚀 백엔드 API로 전송
                        const response = await fetch(`/api/audio/stt?userId=${userId}`, {
                            method: "POST",
                            body: formData,
                        });

                        if (response.ok) {
                            console.log(await response.text());
                            console.log("오디오 업로드 성공!");
                        } else {
                            console.error("오디오 업로드 실패:", response.statusText);
                        }
                    } catch (error) {
                        console.error("API 요청 중 오류 발생:", error);
                    }
                };

            } catch (error) {
                console.error("마이크 접근 오류:", error);
                alert("마이크에 접근할 수 없습니다. 브라우저 설정을 확인하세요.");
            }

        } else {
            // 🔴 녹음 중지 및 저장
            mediaRecorder.stop();
            isRecording = false;
            button.style.filter = "brightness(100%)"; // ✅ 버튼 밝기 원래대로 복원
        }
    });
});