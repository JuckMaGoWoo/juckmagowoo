let mediaRecorder;
let audioChunks = [];
let isRecording = false;

document.addEventListener("DOMContentLoaded", () => {
    const button = document.getElementById("recordBtn");

    button.addEventListener("click", async () => {
        if (!isRecording) {
            try {

                const stream = await navigator.mediaDevices.getUserMedia({ audio: true });

                mediaRecorder = new MediaRecorder(stream);
                mediaRecorder.start();
                isRecording = true;
                button.style.filter = "brightness(50%)";

                mediaRecorder.ondataavailable = event => {
                    audioChunks.push(event.data);
                };

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
                            //audio.controls = true; // 플레이어 추가
                            //document.body.appendChild(audio); // 브라우저에 추가

                            audio.oncanplaythrough = () => {
                                audio.play().catch(error => console.error("자동 재생 실패:", error));
                            };

                            console.log("오디오 자동 재생 시작!");
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
            mediaRecorder.stop();
            isRecording = false;
            button.style.filter = "brightness(100%)";
        }
    });
});

