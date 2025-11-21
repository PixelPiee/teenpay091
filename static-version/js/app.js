// Configuration
const API_BASE_URL = "https://teenpay091.onrender.com"; // Production Render URL
// const API_BASE_URL = "http://localhost:8000"; // Use this for local testing

// State
let currentUser = {};
let friends = [];
let transactions = [];

// DOM Elements
const els = {
    userName: document.getElementById('user-name'),
    userUpi: document.getElementById('user-upi'),
    userBalance: document.getElementById('user-balance'),
    mainAvatarContainer: document.getElementById('main-avatar-container'),
    friendsRail: document.getElementById('friends-rail'),
    receiverSelect: document.getElementById('receiver-select'),
    timelineList: document.getElementById('timeline-list'),
    timelineCount: document.getElementById('timeline-count'),
    sendMoneyForm: document.getElementById('send-money-form'),
    profileUpload: document.getElementById('profile-upload')
};

// API Functions
async function fetchData() {
    try {
        const [userRes, friendsRes, txRes] = await Promise.all([
            fetch(`${API_BASE_URL}/api/user`),
            fetch(`${API_BASE_URL}/api/friends`),
            fetch(`${API_BASE_URL}/api/transactions`)
        ]);

        currentUser = await userRes.json();
        friends = await friendsRes.json();
        transactions = await txRes.json();

        renderUser();
        renderFriends();
        renderTimeline();
    } catch (error) {
        console.error("Error fetching data:", error);
        alert("Failed to load data from server. Is the backend running?");
    }
}

// Render Functions
function renderUser() {
    els.userName.textContent = currentUser.name;
    els.userUpi.textContent = currentUser.upiId;
    els.userBalance.textContent = `₹${currentUser.balance.toFixed(2)}`;

    let avatarHtml;
    if (currentUser.profilePictureUrl) {
        avatarHtml = `<div class="relative flex size-12 shrink-0 items-center justify-center overflow-hidden rounded-full border-2 border-white shadow-sm">
            <img src="${currentUser.profilePictureUrl}" alt="${currentUser.name}" class="h-full w-full object-cover" />
        </div>`;
    } else {
        avatarHtml = `<div class="relative flex size-12 shrink-0 items-center justify-center overflow-hidden rounded-full border-2 border-white shadow-sm" style="background-color: hsl(${currentUser.avatarHue}, 85%, 75%)">
            <span class="text-lg font-bold text-white drop-shadow-sm">${currentUser.name.charAt(0)}</span>
        </div>`;
    }
    els.mainAvatarContainer.innerHTML = avatarHtml;
}

function renderFriends() {
    els.friendsRail.innerHTML = friends.map(friend => {
        let avatarHtml;
        if (friend.profilePictureUrl) {
            avatarHtml = `<div class="relative flex size-14 shrink-0 items-center justify-center overflow-hidden rounded-full border-2 border-white shadow-sm transition-transform hover:scale-110">
                <img src="${friend.profilePictureUrl}" alt="${friend.name}" class="h-full w-full object-cover" />
            </div>`;
        } else {
            avatarHtml = `<div class="relative flex size-14 shrink-0 items-center justify-center overflow-hidden rounded-full border-2 border-white shadow-sm transition-transform hover:scale-110" style="background-color: hsl(${friend.avatarHue}, 85%, 75%)">
                <span class="text-xl font-bold text-white drop-shadow-sm">${friend.name.charAt(0)}</span>
            </div>`;
        }
        return `<div class="flex flex-col items-center gap-2 min-w-[4rem] cursor-pointer" onclick="selectFriend('${friend.upiId}')">
            ${avatarHtml}
            <span class="text-xs font-medium text-slate-200">${friend.name.split(' ')[0]}</span>
        </div>`;
    }).join('');

    els.receiverSelect.innerHTML = friends.map(friend =>
        `<option value="${friend.upiId}" style="background-color: #1e293b;">${friend.name} (${friend.upiId})</option>`
    ).join('');
}

function renderTimeline() {
    els.timelineCount.textContent = transactions.length;

    if (transactions.length === 0) {
        els.timelineList.innerHTML = '<div class="pl-14 text-slate-500">No transactions yet. Start the vibe!</div>';
        return;
    }

    els.timelineList.innerHTML = transactions.map(tx => {
        const isSender = tx.sender === currentUser.upiId;
        const otherUserUpi = isSender ? tx.receiver : tx.sender;
        const otherUser = friends.find(f => f.upiId === otherUserUpi) || { name: "Unknown", avatarHue: 0 };

        const colorClass = isSender ? "bg-orange-400 ring-orange-50" : "bg-emerald-400 ring-emerald-50";
        const amountClass = isSender ? "text-slate-900" : "text-emerald-600";
        const sign = isSender ? "-" : "+";
        const desc = isSender ? `Paid ${otherUser.name}` : `Received from ${otherUser.name}`;

        // Parse date from backend (ISO string)
        const date = new Date(tx.createdAt);
        const dateStr = new Intl.DateTimeFormat('en-US', { month: 'short', day: 'numeric', hour: 'numeric', minute: 'numeric' }).format(date);

        return `<div class="relative pl-14 pb-6 last:pb-0">
            <div class="absolute left-[1.35rem] top-4 size-2.5 rounded-full border-2 border-white bg-slate-300 ring-4 ring-white ${colorClass}"></div>
            <div class="group relative overflow-hidden rounded-2xl border border-white/50 bg-white/40 p-4 transition-all hover:bg-white hover:shadow-md">
                <div class="flex items-start justify-between gap-4">
                    <div class="flex items-center gap-3">
                        <div class="relative flex size-10 shrink-0 items-center justify-center overflow-hidden rounded-full border-2 border-white shadow-sm" style="background-color: hsl(${otherUser.avatarHue}, 85%, 75%)">
                            <span class="text-sm font-bold text-white drop-shadow-sm">${otherUser.name.charAt(0)}</span>
                        </div>
                        <div>
                            <p class="font-medium text-slate-900">${desc}</p>
                            <p class="text-xs text-slate-500">${dateStr}</p>
                        </div>
                    </div>
                    <div class="text-right">
                        <p class="font-bold ${amountClass}">${sign}₹${tx.amount}</p>
                        <p class="text-xs text-slate-500 italic">${tx.note || ''}</p>
                    </div>
                </div>
            </div>
        </div>`;
    }).join('');
}

// Actions
window.selectFriend = (upiId) => {
    els.receiverSelect.value = upiId;
    els.sendMoneyForm.scrollIntoView({ behavior: 'smooth' });
};

els.sendMoneyForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const formData = new FormData(e.target);
    const amount = parseFloat(formData.get('amount'));
    const receiver = formData.get('receiverUpiId');
    const note = formData.get('note');

    if (amount > currentUser.balance) {
        alert("Insufficient balance!");
        return;
    }

    try {
        const res = await fetch(`${API_BASE_URL}/api/send-money`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                receiverUpiId: receiver,
                amount: amount,
                note: note
            })
        });

        if (res.ok) {
            alert(`Successfully sent ₹${amount}!`);
            e.target.reset();
            fetchData(); // Refresh data
        } else {
            alert("Transaction failed!");
        }
    } catch (error) {
        console.error("Error sending money:", error);
        alert("Network error!");
    }
});

els.profileUpload.addEventListener('change', (e) => {
    const file = e.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = (e) => {
            // Simulate upload by setting Data URL locally for now
            // In a real app, we would POST this to /api/upload-picture
            currentUser.profilePictureUrl = e.target.result;
            renderUser();
        };
        reader.readAsDataURL(file);
    }
});

// Init
fetchData();
