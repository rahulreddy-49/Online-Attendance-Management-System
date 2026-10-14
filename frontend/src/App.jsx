import React, { useState } from 'react';
import { useAuth } from './context/AuthContext';
import { Sidebar } from './components/Sidebar';
import { Topbar } from './components/Topbar';
import { Toast } from './components/Toast';

import { Login } from './pages/Login';
import { AdminDashboard } from './pages/AdminDashboard';
import { StudentsManagement } from './pages/StudentsManagement';
import { TeachersManagement } from './pages/TeachersManagement';
import { SubjectsManagement } from './pages/SubjectsManagement';
import { AssignmentsManagement } from './pages/AssignmentsManagement';
import { TeacherDashboard } from './pages/TeacherDashboard';
import { MarkAttendance } from './pages/MarkAttendance';
import { AttendanceHistory } from './pages/AttendanceHistory';
import { Reports } from './pages/Reports';

export function App() {
  const { user, isAdmin } = useAuth();
  const [currentView, setCurrentView] = useState(() => (isAdmin ? 'admin-dashboard' : 'teacher-dashboard'));
  const [selectedAssignment, setSelectedAssignment] = useState(null);
  const [toast, setToast] = useState(null);

  const showToast = (message, type = 'success') => {
    setToast({ message, type });
    setTimeout(() => {
      setToast(null);
    }, 4000);
  };

  // If not authenticated, render Login
  if (!user) {
    return <Login />;
  }

  // Titles per view
  const titles = {
    'admin-dashboard': 'Institutional Administration Overview',
    'students': 'Student Information & Enrollment',
    'teachers': 'Faculty Members & Instructors',
    'subjects': 'Curriculum Catalog & Course Modules',
    'assignments': 'Teacher - Subject Class Assignments',
    'teacher-dashboard': 'Faculty Instructor Workspace',
    'mark-attendance': 'Class Attendance Sheet Roll',
    'attendance-history': 'Attendance Sessions Audit History',
    'reports': 'Attendance Intelligence & Defaulter Reports'
  };

  const getQuickAction = () => {
    if (isAdmin) {
      if (currentView === 'students') return null;
      return { label: '+ Take Attendance', action: () => setCurrentView('mark-attendance') };
    }
    return { label: '+ Take Attendance', action: () => setCurrentView('mark-attendance') };
  };

  const quick = getQuickAction();

  return (
    <div className="app-layout">
      <Sidebar currentView={currentView} setCurrentView={setCurrentView} />

      <div className="app-main">
        <Topbar
          title={titles[currentView] || 'Online Attendance System'}
          quickActionLabel={quick?.label}
          onQuickAction={quick?.action}
        />

        <main className="workspace-body">
          {currentView === 'admin-dashboard' && <AdminDashboard setCurrentView={setCurrentView} />}
          {currentView === 'students' && <StudentsManagement showToast={showToast} />}
          {currentView === 'teachers' && <TeachersManagement showToast={showToast} />}
          {currentView === 'subjects' && <SubjectsManagement showToast={showToast} />}
          {currentView === 'assignments' && <AssignmentsManagement showToast={showToast} />}

          {currentView === 'teacher-dashboard' && (
            <TeacherDashboard setCurrentView={setCurrentView} setSelectedAssignment={setSelectedAssignment} />
          )}
          {currentView === 'mark-attendance' && (
            <MarkAttendance
              showToast={showToast}
              setCurrentView={setCurrentView}
              initialAssignment={selectedAssignment}
            />
          )}
          {currentView === 'attendance-history' && (
            <AttendanceHistory setCurrentView={setCurrentView} />
          )}
          {currentView === 'reports' && <Reports showToast={showToast} />}
        </main>
      </div>

      <Toast toast={toast} />
    </div>
  );
}

export default App;
