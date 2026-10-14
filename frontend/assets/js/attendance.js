/**
 * Online Attendance Management System - Interactive Attendance Sheet Engine
 */

document.addEventListener('DOMContentLoaded', () => {
    const classSelector = document.getElementById('assignmentSelect');
    const dateInput = document.getElementById('sessionDate');
    const loadBtn = document.getElementById('loadStudentsBtn');
    const sheetContainer = document.getElementById('attendanceSheetContainer');
    const emptyState = document.getElementById('emptyState');
    const attendanceForm = document.getElementById('attendanceForm');

    if (!loadBtn || !classSelector || !dateInput) return;

    // Load students trigger
    loadBtn.addEventListener('click', () => {
        const assignmentId = classSelector.value;
        const dateVal = dateInput.value;

        if (!assignmentId) {
            alert('Please select a class assignment first.');
            return;
        }
        if (!dateVal) {
            alert('Please pick a session date.');
            return;
        }

        loadStudents(assignmentId, dateVal);
    });

    // Auto load if both are filled
    classSelector.addEventListener('change', () => {
        if (classSelector.value && dateInput.value) {
            loadStudents(classSelector.value, dateInput.value);
        }
    });

    dateInput.addEventListener('change', () => {
        if (classSelector.value && dateInput.value) {
            loadStudents(classSelector.value, dateInput.value);
        }
    });

    function loadStudents(assignmentId, dateVal) {
        loadBtn.disabled = true;
        loadBtn.innerText = 'Loading...';

        const url = `${window.contextPath || ''}/teacher/attendance/load-students?assignmentId=${encodeURIComponent(assignmentId)}&date=${encodeURIComponent(dateVal)}`;

        fetch(url)
            .then(res => res.json())
            .then(data => {
                loadBtn.disabled = false;
                loadBtn.innerText = 'Fetch Students';

                if (!data.success) {
                    alert(data.message || 'Failed to load student list.');
                    return;
                }

                renderStudentSheet(data.students, data.existingRecords, data.existingSession);
            })
            .catch(err => {
                loadBtn.disabled = false;
                loadBtn.innerText = 'Fetch Students';
                console.error(err);
                alert('Connection error while fetching student data.');
            });
    }

    function renderStudentSheet(students, existingRecords, isExisting) {
        if (!students || students.length === 0) {
            if (emptyState) emptyState.style.display = 'block';
            if (sheetContainer) sheetContainer.style.display = 'none';
            return;
        }

        if (emptyState) emptyState.style.display = 'none';
        if (sheetContainer) sheetContainer.style.display = 'block';

        const banner = document.getElementById('existingSessionBanner');
        if (banner) {
            banner.style.display = isExisting ? 'block' : 'none';
        }

        const tbody = document.getElementById('studentTableBody');
        tbody.innerHTML = '';

        // Map existing records by student_id
        const recordMap = {};
        if (existingRecords) {
            existingRecords.forEach(r => {
                recordMap[r.studentId] = r.status;
            });
        }

        students.forEach((s, idx) => {
            const tr = document.createElement('tr');
            const studentStatus = recordMap[s.id] || 'PRESENT';

            tr.innerHTML = `
                <td>${idx + 1}</td>
                <td><strong>${escapeHtml(s.rollNumber)}</strong></td>
                <td>${escapeHtml(s.fullName)}</td>
                <td>
                    <div class="status-switch">
                        <input type="radio" id="p_${s.id}" name="status_${s.id}" value="PRESENT" ${studentStatus === 'PRESENT' ? 'checked' : ''} onchange="updateLiveStats()">
                        <label for="p_${s.id}" class="lbl-present">Present</label>

                        <input type="radio" id="a_${s.id}" name="status_${s.id}" value="ABSENT" ${studentStatus === 'ABSENT' ? 'checked' : ''} onchange="updateLiveStats()">
                        <label for="a_${s.id}" class="lbl-absent">Absent</label>
                    </div>
                </td>
                <td>
                    <input type="text" class="form-control" name="remarks_${s.id}" placeholder="Optional note..." style="padding: 0.35rem 0.6rem; font-size: 0.82rem;">
                </td>
            `;
            tbody.appendChild(tr);
        });

        updateLiveStats();
    }

    // Fast action: Mark All Present
    window.markAllPresent = function() {
        const radios = document.querySelectorAll('input[type="radio"][value="PRESENT"]');
        radios.forEach(r => r.checked = true);
        updateLiveStats();
    };

    // Fast action: Mark All Absent
    window.markAllAbsent = function() {
        const radios = document.querySelectorAll('input[type="radio"][value="ABSENT"]');
        radios.forEach(r => r.checked = true);
        updateLiveStats();
    };

    // Live tally calculator
    window.updateLiveStats = function() {
        const total = document.querySelectorAll('#studentTableBody tr').length;
        const present = document.querySelectorAll('#studentTableBody input[value="PRESENT"]:checked').length;
        const absent = total - present;
        const rate = total > 0 ? Math.round((present / total) * 100) : 0;

        const statTotal = document.getElementById('tallyTotal');
        const statPresent = document.getElementById('tallyPresent');
        const statAbsent = document.getElementById('tallyAbsent');
        const statRate = document.getElementById('tallyRate');

        if (statTotal) statTotal.innerText = total;
        if (statPresent) statPresent.innerText = present;
        if (statAbsent) statAbsent.innerText = absent;
        if (statRate) statRate.innerText = `${rate}%`;
    };

    // Save Attendance Form Submit
    if (attendanceForm) {
        attendanceForm.addEventListener('submit', (e) => {
            e.preventDefault();

            const rows = document.querySelectorAll('#studentTableBody tr');
            if (rows.length === 0) {
                alert('No students to submit.');
                return;
            }

            const assignmentId = classSelector.value;
            const sessionDate = dateInput.value;

            const records = [];
            rows.forEach(tr => {
                const radio = tr.querySelector('input[type="radio"]:checked');
                const remarksInput = tr.querySelector('input[name^="remarks_"]');
                if (radio) {
                    const studentId = parseInt(radio.name.replace('status_', ''));
                    records.push({
                        studentId: studentId,
                        status: radio.value,
                        remarks: remarksInput ? remarksInput.value.trim() : ''
                    });
                }
            });

            const payload = {
                assignmentId: parseInt(assignmentId),
                sessionDate: sessionDate,
                records: records
            };

            const submitBtn = document.getElementById('saveAttendanceBtn');
            if (submitBtn) {
                submitBtn.disabled = true;
                submitBtn.innerText = 'Saving Attendance...';
            }

            fetch(`${window.contextPath || ''}/teacher/attendance/save`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: new URLSearchParams({
                    assignmentId: assignmentId,
                    sessionDate: sessionDate,
                    recordsJson: JSON.stringify(records)
                })
            })
            .then(res => res.json())
            .then(data => {
                if (submitBtn) {
                    submitBtn.disabled = false;
                    submitBtn.innerText = 'Save Attendance';
                }

                if (data.success) {
                    alert('Attendance successfully recorded!');
                    window.location.href = `${window.contextPath || ''}/teacher/attendance/history`;
                } else {
                    alert('Error: ' + (data.message || 'Could not save attendance.'));
                }
            })
            .catch(err => {
                if (submitBtn) {
                    submitBtn.disabled = false;
                    submitBtn.innerText = 'Save Attendance';
                }
                console.error(err);
                alert('Failed to save attendance. Please check network connection.');
            });
        });
    }

    function escapeHtml(str) {
        if (!str) return '';
        return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
    }
});
