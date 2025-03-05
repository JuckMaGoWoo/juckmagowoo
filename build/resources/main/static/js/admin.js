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
        updateGraph();
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
