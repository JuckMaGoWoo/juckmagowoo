let myChart = null;

document.addEventListener("DOMContentLoaded", function () {
    // ✅ 페이지 로딩 시 기존 사용자 정보 불러오기
    loadSelectedUser();

    // ✅ storage 변경 감지 (다른 탭에서도 변경 가능)
    window.addEventListener("storage", function (event) {
        if (event.key === "selectedUser") {
            loadSelectedUser();
        }
    });

    // ✅ window.postMessage를 통한 데이터 수신 (선택사항)
    window.addEventListener("message", function (event) {
        if (event.data.type === "USER_SELECTED") {
            updateGraph(event.data.data);
        }
    });
});

function loadSelectedUser() {
    const storedUser = localStorage.getItem("selectedUser");
    if (storedUser) {
        const user = JSON.parse(storedUser);
        updateGraph(user);
    }
}

function updateGraph(user) {
    console.log("그래프 업데이트: ", user);
  
    userInfo(user);
  
    main(user['userId']);
    // 여기에 그래프를 업데이트하는 코드 추가 (예: Chart.js 사용)
}



async function fetchDataFromDB(userId) {
    try {
        const response = await fetch(`/api/v1/dangerzone/${userId}/all`);
        const data = await response.json();
        return {
            success: true,
            data: data
        };
    } catch (error) {
        throw new Error('데이터를 불러오는 중 오류가 발생했습니다: ' + error.message);
    }
}


function userInfo(data) {
    const url = `https://swan-lake.site/main?userId=${data['userId']}`;
    document.getElementById("detailName").innerHTML = `이름: ${data['name']}`;
    document.getElementById("detailAge").innerHTML = `나이: ${data['age']}`;
    document.getElementById("detailSex").innerHTML = `성별: ${data['sex'] ? '남자' : '여자'}`;
    document.getElementById("url").innerHTML = url;
    const qrCode = new QRCodeStyling({
        width: 90,
        height: 90,
        data: url,
        dotsOptions: {
            color: "#000",
            type: "square"
        },
        backgroundOptions: {
            color: "#fff"
        }
    });
    document.getElementById("qrcode").innerHTML = '';
    qrCode.append(document.getElementById("qrcode"));
}


function initChart(data) {
    const ctx = document.getElementById('dataGraph').getContext('2d');

    // 기존 차트가 있으면 파괴
    if (myChart) {
        myChart.destroy();
    }

    // 차트 데이터 준비
    const labels = data.map(item => {
        const date = new Date(item.createdAt);
        return date.getHours() + ':' + date.getMinutes().toString().padStart(2, '0');
    });

    const negativeScores = data.map(item => item.anxietyScore);
    const logicalScores = data.map(item => item.logicalScore);

    // Chart.js 커스텀 플러그인 정의
    const backgroundHighlightPlugin = {
        id: 'backgroundHighlight',
        beforeDraw: (chart) => {
            const {ctx, chartArea, scales} = chart;
            const yScale = scales.y;
            const threshold = 70; // 하이라이트할 임계값

            if (!chartArea) {
                return;
            }

            // 임계값의 Y 위치 계산
            const thresholdY = yScale.getPixelForValue(threshold);

            // 임계값 위쪽 영역 채우기
            ctx.save();
            ctx.fillStyle = 'rgba(255, 200, 200, 0.4)'; // 하이라이트 색상
            ctx.fillRect(
                chartArea.left,
                chartArea.top,
                chartArea.width,
                thresholdY - chartArea.top
            );
            ctx.restore();
        }
    };

    // 기존 차트 코드에 플러그인 등록
    Chart.register(backgroundHighlightPlugin);

    // 차트 생성
    myChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [
                {
                    label: 'Negative Score',
                    data: negativeScores,
                    borderColor: 'rgba(255, 99, 132, 1)',
                    backgroundColor: 'rgba(255, 99, 132, 0.2)',
                    borderWidth: 2,
                    pointRadius: 1,
                    pointHoverRadius: 5,
                    tension: 0.3
                },
                {
                    label: 'Logical Score',
                    data: logicalScores,
                    borderColor: 'rgba(54, 162, 235, 1)',
                    backgroundColor: 'rgba(54, 162, 235, 0.2)',
                    borderWidth: 2,
                    pointRadius: 1,
                    pointHoverRadius: 5,
                    tension: 0.3
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                tooltip: {
                    mode: 'index',
                    intersect: false
                },
                legend: {
                    position: 'top',
                }
            },
            scales: {
                x: {
                    title: {
                        display: true,
                        text: '시간'
                    }
                },
                y: {
                    beginAtZero: true,
                    max: 100,
                    title: {
                        display: true,
                        text: '점수'
                    }
                }
            },
            interaction: {
                intersect: false,
                mode: 'index'
            }
        }
    });

    updateTable(negativeScores, logicalScores);
    updatePercentage (negativeScores, logicalScores)
}

// 점수 분포 테이블 업데이트 함수
function updateTable(negativeScores, logicalScores) {
    // 범위 정의 (0-9, 10-19, ..., 90-100)
    const ranges = [
        { min: 0, max: 9 },
        { min: 10, max: 19 },
        { min: 20, max: 29 },
        { min: 30, max: 39 },
        { min: 40, max: 49 },
        { min: 50, max: 59 },
        { min: 60, max: 69 },
        { min: 70, max: 79 },
        { min: 80, max: 89 },
        { min: 90, max: 100 }
    ];

    // 각 범위별 점수 개수 계산
    const negativeCounts = ranges.map(range => {
        return negativeScores.filter(score => score >= range.min && score <= range.max).length;
    });

    const logicalCounts = ranges.map(range => {
        return logicalScores.filter(score => score >= range.min && score <= range.max).length;
    });

    // 테이블 HTML 생성
    let tableHTML = `
        <table>
            <thead>
                <tr>
                    <th>유형 / 범위</th>
                    ${ranges.map(range => `<th>${range.min}-${range.max}</th>`).join('')}
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td><strong>Negative Score</strong></td>
                    ${negativeCounts.map(count => `<td>${count}</td>`).join('')}
                </tr>
                <tr>
                    <td><strong>Logical Score</strong></td>
                    ${logicalCounts.map(count => `<td>${count}</td>`).join('')}
                </tr>
            </tbody>
        </table>
    `;

    // 테이블 삽입
    document.getElementById('dataTable').innerHTML = tableHTML;
}

// 점수 퍼센트 함수
function updatePercentage (negativeScores, logicalScores) {
    // 70점이 넘는 점수 계산
    const negativeCounts = negativeScores.filter(score => score >= 70).length;
    const logicalCounts = logicalScores.filter(score => score >= 70).length;
    const negativeAll = negativeScores.length;
    const logicalAll = logicalScores.length;
    let negativePercent = (negativeCounts/negativeAll)*100;
    let logicalPercent = (logicalCounts/logicalAll)*100;

    // 비율에 따라 적절한 이모지를 반환하는 함수
    function getEmojiByPercentage(percent) {
        if (percent < 25) {
            return "../images/emoji-good.png"; // 웃는 얼굴 (25% 미만)
        } else if (percent >= 25 && percent < 40) {
            return "../images/emoji-normal.png"; // 무표정 (25%~40% 사이)
        } else {
            return "../images/emoji-bad.png"; // 우는 얼굴 (40% 이상)
        }
    }

    // 비율에 따른 이모지를 HTML에 적용
    let percentHTML = `
    <div>
        negative 비율 : ${negativePercent.toFixed(2)}% <img src="${getEmojiByPercentage(negativePercent)}" class="emotion-img" alt="감정이미지">
    </div>
    <div>
        logical 비율 : ${logicalPercent.toFixed(2)}% <img src="${getEmojiByPercentage(logicalPercent)}" class="emotion-img" alt="감정이미지">
    </div>
`;

    // 퍼센트 삽입
    document.getElementById('dataPercent').innerHTML = percentHTML;
}


// 메인 실행 함수
async function main(userId) {
    try {
        // DB에서 데이터 가져오기
        const response = await fetchDataFromDB(userId);

        // 응답 확인
        if (response.success && response.data) {
            // 차트 초기화
            initChart(response.data);
        } else {
            console.log('데이터를 불러오는 데 실패했습니다.');
        }
    } catch (error) {
        console.log(error.message);
    }
}

// 페이지 로드 시 실행
//window.addEventListener('DOMContentLoaded', main);

// 윈도우 리사이즈 시 차트 크기 조정
window.addEventListener('resize', function() {
    if (myChart) {
        myChart.resize();
    }
});

//setInterval(main, 5 * 60 * 1000);