const storedUser = localStorage.getItem("selectedUser");
const user = JSON.parse(storedUser);
const userId = user.userId;

async function loadDangerData(userId) {
    try {
        // userId를 URL 경로로 전달
        const response = await fetch(`/api/v1/dangerzone/${userId}`);
        console.log(response);
        if (!response.ok) {
            throw new Error(`서버 오류: ${response.status}`);
        }

        const dangerData = await response.json();
        console.log(dangerData);
        // 테이블 업데이트
        updateDangerTable(dangerData);
    } catch (error) {
        console.error("위험 데이터를 불러오는 중 오류 발생:", error);
        alert("위험 데이터를 불러오는 중 오류가 발생했습니다.");
    }
}

// 위험 테이블 업데이트 함수
function updateDangerTable(dangerData) {
    let tableHTML = `
        <table>
            <thead>
                <tr>
                    <th style="min-width: 50px">순번</th>
                    <th style="min-width: 250px">사용자 입력</th>
                    <th style="min-width: 250px">시스템 입력</th>
                    <th style="min-width: 100px">불안 지수</th>
                    <th style="min-width: 100px">논리 지수</th>
                </tr>
            </thead>
            <tbody>
    `;

    // dangerData 배열을 순회하여 테이블에 내용 추가
    dangerData.forEach((item, index) => {
        tableHTML += `
            <tr>
                <td>${index + 1}</td>
                <td>${item.userInput}</td>
                <td>${item.gptOutput}</td> <!-- 시스템 입력은 'gptOutput'으로 수정 -->
                <td>${item.anxietyScore}</td> <!-- 우울 척도 (anxietyScore) -->
                <td>${item.logicalScore}</td> <!-- 언어 능력 (logicalScore) -->
            </tr>
        `;
    });

    tableHTML += `</tbody></table>`;

    // 테이블을 HTML 요소에 삽입
    document.getElementById('dangerTable').innerHTML = tableHTML;
}

