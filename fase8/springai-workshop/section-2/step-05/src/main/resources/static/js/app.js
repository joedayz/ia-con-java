// Car Management UI JavaScript

// Global variables for sorting and filtering
let currentSortColumn = 'id';
let currentSortDirection = 'asc';
let carsData = []; // Store the cars data globally for sorting
let currentFilterText = '';
let currentFilterField = 'all';
let lastUpdatedCarId = null; // Track the last updated car for highlighting

// Wait for the DOM to be fully loaded
document.addEventListener('DOMContentLoaded', function() {
    // Load all cars and populate the tables
    loadAllCars();
    
    // Add event listeners for form submissions
    setupEventListeners();

    // Set up sorting functionality
    setupSorting();
    
    // Start polling for approvals (always active now with modal)
    startApprovalPolling();
});

// Function to load all cars from the API
function loadAllCars() {
    fetch('/cars')
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(cars => {
            // Store cars data globally for sorting
            carsData = cars;
            
            // Sort the data if a sort is active
            sortCars();
            
            // Process the cars data
            populateFleetStatusTable(carsData);
        })
        .catch(error => {
            console.error('Error fetching cars:', error);
            displayError('Failed to load car data. Please try again later.');
        });
}

// Function to set up sorting functionality
function setupSorting() {
    const sortableHeaders = document.querySelectorAll('.sortable');
    
    sortableHeaders.forEach(header => {
        header.addEventListener('click', function() {
            const column = this.getAttribute('data-sort');
            
            // If clicking the same column, toggle direction
            if (column === currentSortColumn) {
                currentSortDirection = currentSortDirection === 'asc' ? 'desc' : 'asc';
            } else {
                // New column, default to ascending
                currentSortColumn = column;
                currentSortDirection = 'asc';
            }
            
            // Update header classes for visual indication
            updateSortHeaders();
            
            // Sort and redisplay data
            sortCars();
            populateFleetStatusTable(carsData);
        });
    });
}

// Function to update sort header classes
function updateSortHeaders() {
    // Remove all sort classes
    document.querySelectorAll('.sortable').forEach(header => {
        header.classList.remove('sort-asc', 'sort-desc');
    });
    
    // Add class to current sort column
    const currentHeader = document.querySelector(`.sortable[data-sort="${currentSortColumn}"]`);
    if (currentHeader) {
        currentHeader.classList.add(currentSortDirection === 'asc' ? 'sort-asc' : 'sort-desc');
    }
}

// Function to sort cars based on current sort settings
function sortCars() {
    carsData.sort((a, b) => {
        let valueA, valueB;
        
        // Handle special case for status which needs to be displayed text
        if (currentSortColumn === 'status') {
            valueA = getStatusDisplay(a.status);
            valueB = getStatusDisplay(b.status);
        } else {
            valueA = a[currentSortColumn];
            valueB = b[currentSortColumn];
        }
        
        // Handle numeric values
        if (currentSortColumn === 'id' || currentSortColumn === 'year') {
            valueA = Number(valueA) || 0;
            valueB = Number(valueB) || 0;
        }
        
        // Compare values based on direction
        if (valueA < valueB) {
            return currentSortDirection === 'asc' ? -1 : 1;
        }
        if (valueA > valueB) {
            return currentSortDirection === 'asc' ? 1 : -1;
        }
        return 0;
    });
}

// Function to filter cars based on current filter settings
function filterCars() {
    if (!currentFilterText) {
        return carsData; // Return all cars if no filter text
    }
    
    return carsData.filter(car => {
        // Convert filter text to lowercase for case-insensitive comparison
        const filterText = currentFilterText.toLowerCase();
        
        // If filtering on a specific field
        if (currentFilterField !== 'all') {
            let fieldValue = car[currentFilterField];
            
            // Handle special case for status which needs to be displayed text
            if (currentFilterField === 'status') {
                fieldValue = getStatusDisplay(fieldValue);
            }
            
            // Convert to string and check if it contains the filter text
            return String(fieldValue).toLowerCase().includes(filterText);
        }
        
        // If filtering across all fields
        return (
            String(car.id).toLowerCase().includes(filterText) ||
            car.make.toLowerCase().includes(filterText) ||
            car.model.toLowerCase().includes(filterText) ||
            String(car.year).toLowerCase().includes(filterText) ||
            (car.condition && car.condition.toLowerCase().includes(filterText)) ||
            getStatusDisplay(car.status).toLowerCase().includes(filterText)
        );
    });
}

// Function to populate the Fleet Status table
function populateFleetStatusTable(cars) {
    const tableBody = document.getElementById('fleet-status-table-body');
    tableBody.innerHTML = ''; // Clear existing rows
    
    // Apply filter if there's filter text
    const filteredCars = currentFilterText ? filterCars() : cars;
    
    if (filteredCars.length === 0) {
        tableBody.innerHTML = '<tr><td colspan="7">No cars match your filter criteria</td></tr>';
        return;
    }
    
    filteredCars.forEach(car => {
        const row = document.createElement('tr');
        
        // Highlight the row if it was just updated
        if (car.id === lastUpdatedCarId) {
            row.classList.add('highlight-row');
            // Clear the highlight after animation completes
            setTimeout(() => {
                lastUpdatedCarId = null;
            }, 3000);
        }
        
        // Get status pill class based on car status
        const statusPillClass = getStatusPillClass(car.status);
        
        let actionCell = '';
        if (car.status === 'RENTED' || car.status === 'AT_CLEANING' || car.status === 'IN_MAINTENANCE') {
            actionCell = `
                <td>
                    <form onsubmit="processFeedback(event, ${car.id}, '${car.status}')">
                        <input type="text" class="feedback-input" id="feedback-${car.id}" placeholder="Enter feedback">
                        <button type="submit" class="return-button">Return</button>
                    </form>
                </td>`;
        } else if (car.status === 'PENDING_DISPOSITION') {
            actionCell = `<td><em>Awaiting human approval</em></td>`;
        } else {
            actionCell = `<td></td>`;
        }

        row.innerHTML = `
            <td>${car.id}</td>
            <td>${car.make}</td>
            <td>${car.model}</td>
            <td>${car.year}</td>
            <td>${car.condition || 'N/A'}</td>
            <td><span class="status-pill ${statusPillClass}">${getStatusDisplay(car.status)}</span></td>
            ${actionCell}
        `;
        
        tableBody.appendChild(row);
    });
}

// Function to process feedback and return a car from any status
function processFeedback(event, carId, status) {
    event.preventDefault();
    const feedback = document.getElementById(`feedback-${carId}`).value;
    const button = event.target.querySelector('button');

    button.disabled = true;
    button.classList.add('loading');
    const originalText = button.textContent;
    button.textContent = 'Processing...';
    showNotification('Processing return… approval dialog will open if human review is required.');

    const statusLabels = {
        'RENTED': 'rental',
        'AT_CLEANING': 'cleaning',
        'IN_MAINTENANCE': 'maintenance'
    };

    userDismissedApprovalModal = false;

    fetch(`/car-management/return/${carId}?feedback=${encodeURIComponent(feedback)}`, { method: 'POST' })
    .then(response => {
        if (response.status === 202) {
            return response.json().then(body => ({ async: true, body }));
        }
        if (response.status === 409) {
            return response.json().then(body => ({ conflict: true, body }));
        }
        if (!response.ok) throw new Error('Network response was not ok');
        return response.text().then(text => ({ async: false, text }));
    })
    .then(result => {
        if (result.conflict) {
            button.disabled = false;
            button.classList.remove('loading');
            button.textContent = originalText;
            showNotification(result.body.message || 'Return already in progress.');
            waitForReturnCompletion(carId, button, originalText, statusLabels[status]);
            return;
        }
        if (result.async) {
            button.textContent = 'Awaiting approval…';
            button.classList.remove('loading');
            showNotification(result.body.message || 'Processing — complete approval in the dialog.');
            waitForReturnCompletion(carId, button, originalText, statusLabels[status]);
            return;
        }
        lastUpdatedCarId = carId;
        showNotification(`Car successfully returned from ${statusLabels[status]}`);
        loadAllCars();
        button.disabled = false;
        button.classList.remove('loading');
        button.textContent = originalText;
        stopFastApprovalPolling();
    })
    .catch(error => {
        console.error(`Error returning car from ${statusLabels[status]}:`, error);
        displayError(`Failed to process ${statusLabels[status]} return. Please try again.`);
        button.disabled = false;
        button.classList.remove('loading');
        button.textContent = originalText;
        stopFastApprovalPolling();
    });
}

// Tras 202: un solo intervalo (~1.5s) hasta que el job en servidor termine
function waitForReturnCompletion(carId, button, originalText, statusLabel) {
    stopFastApprovalPolling();

    const finish = (message) => {
        stopFastApprovalPolling();
        rescheduleBackgroundApprovalPoll();
        lastUpdatedCarId = carId;
        showNotification(message || `Car successfully returned from ${statusLabel}`);
        loadAllCars();
        button.disabled = false;
        button.classList.remove('loading');
        button.textContent = originalText;
    };

    const check = () => {
        Promise.all([
            fetchPendingApprovals().catch(() => []),
            fetch(`/car-management/return/${carId}/status?_=${Date.now()}`)
                .then(r => r.ok ? r.json() : { state: 'RUNNING' })
                .catch(() => ({ state: 'RUNNING' }))
        ]).then(([proposals, jobStatus]) => {
            const state = jobStatus.state || jobStatus.State || 'RUNNING';
            const carPending = proposals.filter(p => Number(p.carNumber) === Number(carId));
            if (carPending.length > 0) {
                userDismissedApprovalModal = false;
                showApprovalModal(proposals);
                loadAllCars();
            }
            updateApprovalUi(proposals, { forceModal: carPending.length > 0 });
            if (state === 'FAILED') {
                displayError('Return failed: ' + (jobStatus.message || 'Unknown error'));
                stopFastApprovalPolling();
                rescheduleBackgroundApprovalPoll();
                button.disabled = false;
                button.classList.remove('loading');
                button.textContent = originalText;
                return;
            }
            if (state === 'AWAITING_APPROVAL') {
                button.textContent = 'Awaiting approval…';
            }
            if (state === 'COMPLETED') {
                finish(`Car successfully returned from ${statusLabel}`);
            }
        }).catch(err => console.error('Waiting for return completion:', err));
    };

    hitlPollingActive = true;
    check();
    fastApprovalPollingInterval = setInterval(check, 1500);
}

// Helper function to get CSS class based on car status
function getStatusClass(status) {
    switch(status) {
        case 'RENTED':
            return 'status-rented';
        case 'AT_CLEANING':
            return 'status-cleaning';
        case 'IN_MAINTENANCE':
            return 'status-maintenance';
        case 'AVAILABLE':
            return 'status-available';
        case 'PENDING_DISPOSITION':
            return 'status-disposition';
        default:
            return '';
    }
}

// Helper function to get status pill class based on car status
function getStatusPillClass(status) {
    switch(status) {
        case 'RENTED':
            return 'status-pill-rented';
        case 'AT_CLEANING':
            return 'status-pill-cleaning';
        case 'IN_MAINTENANCE':
            return 'status-pill-maintenance';
        case 'AVAILABLE':
            return 'status-pill-available';
        case 'PENDING_DISPOSITION':
            return 'status-pill-disposition';
        default:
            return '';
    }
}

// Helper function to get display text for car status
function getStatusDisplay(status) {
    switch(status) {
        case 'RENTED':
            return 'Rented';
        case 'AT_CLEANING':
            return 'At Cleaning';
        case 'IN_MAINTENANCE':
            return 'In Maintenance';
        case 'AVAILABLE':
            return 'Available to Rent';
        case 'PENDING_DISPOSITION':
            return 'Pending Disposition';
        default:
            return status;
    }
}

// Function to set up event listeners
function setupEventListeners() {
    // Add refresh button event listener
    const refreshButton = document.getElementById('refresh-button');
    if (refreshButton) {
        refreshButton.addEventListener('click', loadAllCars);
    }
    
    // Add filter input event listener
    const filterInput = document.getElementById('fleet-filter');
    if (filterInput) {
        filterInput.addEventListener('input', function() {
            currentFilterText = this.value;
            populateFleetStatusTable(carsData);
        });
    }
    
    // Add filter field select event listener
    const filterField = document.getElementById('filter-field');
    if (filterField) {
        filterField.addEventListener('change', function() {
            currentFilterField = this.value;
            populateFleetStatusTable(carsData);
        });
    }
    
    // Add clear filter button event listener
    const clearFilterButton = document.getElementById('clear-filter');
    if (clearFilterButton) {
        clearFilterButton.addEventListener('click', function() {
            const filterInput = document.getElementById('fleet-filter');
            const filterField = document.getElementById('filter-field');
            
            // Reset filter values
            currentFilterText = '';
            currentFilterField = 'all';
            
            // Reset UI elements
            if (filterInput) filterInput.value = '';
            if (filterField) filterField.value = 'all';
            
            // Refresh table
            populateFleetStatusTable(carsData);
        });
    }
}

// Function to display error messages
function displayError(message) {
    const errorDiv = document.getElementById('error-message');
    if (errorDiv) {
        errorDiv.textContent = message;
        errorDiv.style.display = 'block';
        
        // Hide after 5 seconds
        setTimeout(() => {
            errorDiv.style.display = 'none';
        }, 5000);
    } else {
        alert(message);
    }
}

// Function to show notification messages
function showNotification(message) {
    const notificationDiv = document.getElementById('notification');
    if (notificationDiv) {
        notificationDiv.textContent = message;
        notificationDiv.style.display = 'block';
        
        // Hide after 3 seconds
        setTimeout(() => {
            notificationDiv.style.display = 'none';
        }, 3000);
    }
}



// Poll for pending approvals every 2 seconds
let approvalPollingInterval = null;
let fastApprovalPollingInterval = null;
let hitlPollingActive = false;
let lastApprovalCount = 0;
let lastRenderedProposalKey = '';
let isModalOpen = false;
let userDismissedApprovalModal = false;

function proposalsCacheKey(proposals) {
    if (!proposals || proposals.length === 0) {
        return '';
    }
    return proposals.map(p => p.id).join(',');
}

// ============================================================================
// HUMAN-IN-THE-LOOP APPROVAL FUNCTIONS
// ============================================================================

// XHR evita cola de fetch detrás del POST /return bloqueado en algunos navegadores
function fetchPendingApprovals() {
    return new Promise((resolve, reject) => {
        const xhr = new XMLHttpRequest();
        xhr.open('GET', `/api/approvals/pending?_=${Date.now()}`);
        xhr.onload = () => {
            if (xhr.status >= 200 && xhr.status < 300) {
                try {
                    resolve(JSON.parse(xhr.responseText));
                } catch (e) {
                    reject(e);
                }
            } else {
                reject(new Error(`HTTP ${xhr.status}`));
            }
        };
        xhr.onerror = () => reject(new Error('Network error'));
        xhr.send();
    });
}

// Actualiza botón flotante y modal sin re-renderizar en bucle
function updateApprovalUi(proposals, options = {}) {
    const previousCount = lastApprovalCount;
    const floatBtn = document.getElementById('approval-notification-btn');
    const countBadge = floatBtn.querySelector('.approval-count-badge');
    const newApprovalsArrived = proposals.length > previousCount;
    const cacheKey = proposalsCacheKey(proposals);

    if (proposals.length > 0) {
        floatBtn.style.display = 'flex';
        countBadge.textContent = proposals.length;
    } else {
        floatBtn.style.display = 'none';
        if (isModalOpen) {
            closeApprovalModal();
        }
    }

    const shouldAutoOpen = proposals.length > 0 && !isModalOpen && !userDismissedApprovalModal
        && (options.forceModal || hitlPollingActive || newApprovalsArrived);

    if (shouldAutoOpen) {
        if (newApprovalsArrived) {
            showBrowserNotification('🚨 Approval Required',
                `${proposals.length} vehicle disposition${proposals.length > 1 ? 's' : ''} awaiting your approval`);
        }
        showApprovalModal(proposals);
        lastRenderedProposalKey = cacheKey;
    } else if (isModalOpen && proposals.length > 0 && cacheKey !== lastRenderedProposalKey) {
        refreshApprovalModalBody(proposals);
        lastRenderedProposalKey = cacheKey;
    }

    lastApprovalCount = proposals.length;
    if (!hitlPollingActive) {
        rescheduleBackgroundApprovalPoll();
    }
}

// Load and display pending approvals
async function loadPendingApprovals() {
    try {
        const proposals = hitlPollingActive
            ? await fetchPendingApprovals()
            : await fetch('/api/approvals/pending').then(r => {
                if (!r.ok) throw new Error(`HTTP ${r.status}`);
                return r.json();
            });
        updateApprovalUi(proposals);
    } catch (error) {
        console.error('Error loading pending approvals:', error);
    }
}

function refreshApprovalModalBody(proposals) {
    const modalBody = document.getElementById('approval-modal-body');
    if (!proposals || proposals.length === 0) {
        modalBody.innerHTML = '<p style="text-align: center; padding: 40px; color: #666;">No pending approvals at this time.</p>';
        return;
    }
    modalBody.innerHTML = '';
    proposals.forEach(proposal => {
        modalBody.appendChild(createApprovalCard(proposal));
    });
}

// Abre el modal y rellena el contenido de forma síncrona (evita carrera con isModalOpen)
function showApprovalModal(proposals) {
    const modal = document.getElementById('approval-modal');
    isModalOpen = true;
    userDismissedApprovalModal = false;
    modal.style.display = 'flex';
    document.body.classList.add('approval-modal-open');
    refreshApprovalModalBody(proposals);
}

// Open approval modal (botón flotante)
function openApprovalModal() {
    userDismissedApprovalModal = false;
    loadModalContent().then(proposals => {
        if (proposals && proposals.length > 0) {
            showApprovalModal(proposals);
        } else {
            const modal = document.getElementById('approval-modal');
            isModalOpen = true;
            modal.style.display = 'flex';
            document.body.classList.add('approval-modal-open');
            refreshApprovalModalBody([]);
        }
    });
}

// Close approval modal
function closeApprovalModal() {
    isModalOpen = false;
    userDismissedApprovalModal = true;
    document.getElementById('approval-modal').style.display = 'none';
    document.body.classList.remove('approval-modal-open');
}

// Load modal content (called when opening modal)
async function loadModalContent() {
    try {
        const proposals = hitlPollingActive
            ? await fetchPendingApprovals()
            : await fetch('/api/approvals/pending').then(r => r.json());
        if (isModalOpen) {
            refreshApprovalModalBody(proposals);
        }
        return proposals;
    } catch (error) {
        console.error('Error loading modal content:', error);
        return [];
    }
}

// Show browser notification (requires permission)
function showBrowserNotification(title, body) {
    if (!("Notification" in window)) {
        return;
    }
    
    if (Notification.permission === "granted") {
        new Notification(title, { body, icon: '/favicon.ico' });
    } else if (Notification.permission !== "denied") {
        Notification.requestPermission().then(permission => {
            if (permission === "granted") {
                new Notification(title, { body, icon: '/favicon.ico' });
            }
        });
    }
}

// Create an approval card UI element for a proposal
function createApprovalCard(proposal) {
    const card = document.createElement('div');
    card.className = 'approval-card';
    card.id = `approval-${proposal.id}`;
    
    card.innerHTML = `
        <div class="approval-card-header">
            <div class="vehicle-title">
                <span class="vehicle-icon">🚗</span>
                <h3>${proposal.carYear} ${proposal.carMake} ${proposal.carModel}</h3>
            </div>
            <div class="vehicle-value">${proposal.carValue}</div>
        </div>
        
        <div class="approval-card-body">
            <div class="info-row">
                <span class="info-label">Car #${proposal.carNumber}</span>
                <span class="info-label">Condition: ${proposal.carCondition}</span>
            </div>
            
            <div class="damage-section">
                <div class="section-title">Damage Report</div>
                <div class="damage-text">${proposal.rentalFeedback || 'No feedback provided'}</div>
            </div>
            
            <div class="proposal-section">
                <div class="section-title">AI Recommendation</div>
                <div class="proposal-action">
                    <span class="action-badge">${proposal.proposedDisposition}</span>
                    <span class="action-reason">${proposal.dispositionReason}</span>
                </div>
            </div>
        </div>
        
        <div class="approval-card-footer">
            ${getApprovalButtons(proposal)}
        </div>
    `;
    
    return card;
}

// Get approval buttons - simplified to always show Keep vs Dispose
function getApprovalButtons(proposal) {
    return `
        <button class="btn-approve" onclick="handleProposalDecision(${proposal.id}, 'KEEP_CAR')">
            ✅ Keep & Repair
        </button>
        <button class="btn-reject" onclick="handleProposalDecision(${proposal.id}, 'DISPOSE_CAR')">
            🗑️ Dispose
        </button>
    `;
}

// Handle approval/rejection decision for a proposal
async function handleProposalDecision(proposalId, decision) {
    try {
        const reasonInput = document.getElementById(`reason-${proposalId}`);
        const reason = reasonInput ? reasonInput.value.trim() : '';
        
        const response = await fetch(`/api/approvals/${proposalId}/decide`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                decision: decision, // KEEP_CAR or DISPOSE_CAR
                reason: reason || `${decision === 'KEEP_CAR' ? 'Keep and repair' : 'Dispose'} decision by human reviewer`,
                approvedBy: 'Workshop User'
            })
        });
        
        if (response.ok) {
            const actionText = decision === 'KEEP_CAR' ? 'KEEP & REPAIR' : 'DISPOSE';
            showNotification(`✅ Decision: ${actionText} - Workflow will complete shortly`, 'success');
            
            // Remove the approval card with animation
            const card = document.getElementById(`approval-${proposalId}`);
            if (card) {
                card.style.opacity = '0';
                card.style.transform = 'scale(0.95)';
                setTimeout(() => {
                    card.remove();
                    // Reload approvals to update the display
                    loadPendingApprovals();
                    // Don't reload cars immediately - let the next automatic refresh handle it
                    // This prevents the UI from flickering between states
                }, 300);
            }
        } else {
            const error = await response.json();
            showNotification(`❌ Error: ${error.error || 'Failed to record decision'}`, 'error');
        }
    } catch (error) {
        console.error('Error handling proposal decision:', error);
        showNotification('❌ Error recording decision', 'error');
    }
}

// Polling en reposo: cada 10s si no hay HITL; cada 3s si hay pendientes o return en curso
function rescheduleBackgroundApprovalPoll() {
    if (approvalPollingInterval) {
        clearInterval(approvalPollingInterval);
    }
    const ms = (hitlPollingActive || lastApprovalCount > 0) ? 3000 : 10000;
    approvalPollingInterval = setInterval(loadPendingApprovals, ms);
}

function startApprovalPolling() {
    if ("Notification" in window && Notification.permission === "default") {
        Notification.requestPermission();
    }
    loadPendingApprovals();
    rescheduleBackgroundApprovalPoll();
}

function stopFastApprovalPolling() {
    hitlPollingActive = false;
    if (fastApprovalPollingInterval) {
        clearInterval(fastApprovalPollingInterval);
        fastApprovalPollingInterval = null;
    }
    rescheduleBackgroundApprovalPoll();
}

// Stop polling for pending approvals
function stopApprovalPolling() {
    if (approvalPollingInterval) {
        clearInterval(approvalPollingInterval);
        approvalPollingInterval = null;
    }
}
