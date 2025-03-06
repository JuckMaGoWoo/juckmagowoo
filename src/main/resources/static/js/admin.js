let currentPage = 1;
const pageSize = 2;

document.getElementById("addUser").addEventListener("click", async function () {
    const nameInput = document.getElementById("name");
    const ageInput = document.getElementById("age");
    const genderInput = document.getElementById("gender");

    const name = nameInput.value.trim();
    const age = ageInput.value;
    const gender = genderInput.value;

    if (!name || age === "" || isNaN(Number(age)) || !gender) {
        alert("모든 필드를 입력해주세요.");
        return;
    }

    const userData = {
        name,
        age: Number(age),
        sex: gender === "male"
    };

    try {
        const response = await fetch('/api/v1/user', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(userData)
        });

        if (!response.ok) {
            throw new Error(`서버 오류: ${response.status}`);
        }

        const result = await response.json();
        if (result) {
            alert("사용자 등록 성공");

            // ✅ 입력 필드 초기화
            nameInput.value = "";
            ageInput.value = "";
            genderInput.value = "male"; // 기본값 설정

            // ✅ 현재 페이지 다시 불러오기
            loadUserCards(currentPage);
        } else {
            alert("사용자 추가 실패");
        }
    } catch (error) {
        console.error("사용자 추가 중 오류 발생:", error);
        alert("사용자 추가 중 문제가 발생했습니다.");
    }
});


document.addEventListener("DOMContentLoaded", function() {
    loadUserCards(currentPage);
});

async function loadUserCards(page) {
    try {
        const response = await fetch(`/api/v1/user?page=${page}&size=${pageSize}`);
        if (!response.ok) {
            throw new Error(`서버 오류: ${response.status}`);
        }

        const result = await response.json();
        const users = result.users || []; // users가 없으면 빈 배열로 처리
        const totalUsers = result.totalUsers || 0; // totalUsers가 없으면 0으로 처리
        const totalPages = result.totalPages || 0; // totalPages가 없으면 0으로 처리

        // 사용자 목록을 #userCards에 추가
        const userCardsContainer = document.getElementById("userCards");
        userCardsContainer.innerHTML = "";  // 기존 카드들 초기화

        // 사용자가 있으면 카드 추가
        if (users.length > 0) {
            users.forEach(user => addUserCard(user));
        } else {
            userCardsContainer.innerHTML = "<p>사용자가 없습니다.</p>";  // 사용자가 없을 경우 메시지 출력
        }

        // 페이지네이션 버튼 활성화
        document.getElementById("prevPage").disabled = page <= 1;
        document.getElementById("nextPage").disabled = page >= totalPages;
    } catch (error) {
        console.error("사용자 목록을 불러오는 중 오류가 발생했습니다.", error);
        alert("사용자 목록을 불러오는 중 오류가 발생했습니다.");
    }
}

function addUserCard(user) {
    const userCard = document.createElement("div");
    userCard.classList.add("user-card");

    // ✅ true -> "남자", false -> "여자" 변환
    const genderText = user.sex ? "남자" : "여자";

    userCard.innerHTML = `
        <p>이름: ${user.name}</p>
        <p>나이: ${user.age}</p>
        <p>성별: ${genderText}</p>
    `;

    document.getElementById("userCards").appendChild(userCard);
}


// 페이지네이션 버튼 클릭 처리
document.getElementById("prevPage").addEventListener("click", function() {
    if (currentPage > 1) {
        currentPage--;
        loadUserCards(currentPage);
    }
});

document.getElementById("nextPage").addEventListener("click", function() {
    currentPage++;
    loadUserCards(currentPage);
});
