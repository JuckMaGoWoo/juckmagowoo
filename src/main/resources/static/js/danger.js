let dangerTableHTML = `
        <table>
            <thead>
                <tr>
                    <th style="min-width: 50px">순번</th>
                    <th style="min-width: 250px">사용자 입력</th>
                    <th style="min-width: 250px">시스템 입력</th>
                    <th style="min-width: 100px">우울 척도</th>
                    <th style="min-width: 100px">언어 능력</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>1</td>
                    <td>Hi, I'm eddie</td>
                    <td>Bye</td>
                    <td>1</td>
                    <td>1</td>
                </tr>
                <tr>
                    <td>2</td>
                    <td>Hi, I'm eddie</td>
                    <td>Bye</td>
                    <td>3</td>
                    <td>4</td>
                </tr>
            </tbody>
        </table>
    `;

async function main(){
    document.getElementById('dangerTable').innerHTML = dangerTableHTML;
}
window.addEventListener('DOMContentLoaded', main);