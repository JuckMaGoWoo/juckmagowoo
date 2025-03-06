document.getElementById("addUser").addEventListener("click", function() {
    const name = document.getElementById("name").value;
    const age = document.getElementById("age").value;
    const gender = document.getElementById("gender").value;

    if (name && age && gender) {
        // 사용자 정보를 동적으로 추가
        const userCard = document.createElement("div");
        userCard.classList.add("user-card");

        userCard.innerHTML = `
            <p>이름: ${name}</p>
            <p>나이: ${age}</p>
            <p>성별: ${gender}</p>
        `;

        document.getElementById("userCards").appendChild(userCard);

        // 입력 필드 초기화
        document.getElementById("name").value = "";
        document.getElementById("age").value = "";
        document.getElementById("gender").value = "male";

        // 데이터 그래프 업데이트 (예시)
        updateGraph2();
    }
});

// 그래프 업데이트 함수
function updateGraph() {
    const ctx = document.getElementById('dataGraph').getContext('2d');

    // 예시 데이터
    const data = {
        labels: ['User 1', 'User 2', 'User 3'],
        datasets: [{
            label: '사용자 데이터',
            data: [10, 20, 30],
            backgroundColor: 'rgba(75, 192, 192, 0.2)',
            borderColor: 'rgba(75, 192, 192, 1)',
            borderWidth: 1
        }]
    };

    // 기존 차트가 있으면 삭제하고 새로 그리기
    if (window.myChart) {
        window.myChart.destroy();
    }

    // 새 차트 그리기
    window.myChart = new Chart(ctx, {
        type: 'bar',
        data: data,
        options: {
            responsive: true,
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });
}
function updateGraph2() {
    let myChart = null;
    async function fetchDataFromDB() {
        try {
            /*
            const response = await fetch('/api/sentences');
            const data = await response.json();
            return data;
            */
            return new Promise((resolve) => {
                setTimeout(() => {
                    // 실제 환경에서는 서버 API로부터 이 데이터를 받아옵니다
                    resolve(simulateDBResponse());
                }, 1000);
            });
        } catch (error) {
            throw new Error('데이터를 불러오는 중 오류가 발생했습니다: ' + error.message);
        }
    }

    //더미데이터 생성 함수
    function simulateDBResponse() {
        const baseDate = new Date('2025-03-05T10:00:00');
        const result = [];

        for (let i = 1; i <= 30; i++) {
            const date = new Date(baseDate);
            date.setMinutes(date.getMinutes() + (i - 1));

            result.push({
                sentense_id: i,
                user_id: 1,
                negative_score: Math.floor(Math.random() * 100),
                logical_score: Math.floor(Math.random() * 100),
                created_at: date.toISOString()
            });
        }

        return {
            success: true,
            data: result
        };
    }

    function initChart(data) {
        const ctx = document.getElementById('dataGraph').getContext('2d');

        // 기존 차트가 있으면 파괴
        if (myChart) {
            myChart.destroy();
        }

        // 차트 데이터 준비
        const labels = data.map(item => {
            const date = new Date(item.created_at);
            return date.getHours() + ':' + date.getMinutes().toString().padStart(2, '0');
        });

        const negativeScores = data.map(item => item.negative_score);
        const logicalScores = data.map(item => item.logical_score);

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
                    title: {
                        display: true,
                        text: '시간에 따른 점수 변화',
                        font: {
                            size: 16
                        }
                    },
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
        <table class="distribution-table">
            <thead>
                <tr>
                    <th>점수 범위</th>
                    <th>Negative Score</th>
                    <th>Logical Score</th>
                </tr>
            </thead>
            <tbody>
    `;

        // 행 추가
        ranges.forEach((range, i) => {
            tableHTML += `
            <tr>
                <td>${range.min}-${range.max}</td>
                <td>${negativeCounts[i]}</td>
                <td>${logicalCounts[i]}</td>
            </tr>
        `;
        });

        tableHTML += `
            </tbody>
        </table>
    `;

        // 테이블 삽입
        document.getElementById('dataTable').innerHTML = tableHTML;
    }


    // 메인 실행 함수
    async function main() {
        try {
            // DB에서 데이터 가져오기
            const response = await fetchDataFromDB();
            
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
    window.addEventListener('DOMContentLoaded', main);

    // 윈도우 리사이즈 시 차트 크기 조정
    window.addEventListener('resize', function() {
        if (myChart) {
            myChart.resize();
        }
    });

    setInterval(main, 5 * 60 * 1000);
}