let dangerTableHTML = `
        <table>
            <thead>
                <tr>
                    <th>순번</th>
                    <th>사용자 입력</th>
                    <th>시스템 입력</th>
                    <th>우울 척도</th>
                    <th>언어 능력</th>
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