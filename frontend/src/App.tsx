import { useState } from "react";
import type { FormEvent } from "react";

import "./App.css";

import type { JobApplication } from "./types/application";

import LoginPage from "./pages/LoginPage";
import DashboardPage from "./pages/DashboardPage";

import { useAuth } from "./hooks/useAuth";

import { useApplications } from "./hooks/useApplications";
import { getApplicationStats } from "./utils/applicationStats";
import type { ApplicationEvent } from "./types/applicationEvent";
import { getApplicationEvents } from "./services/api";

import type { EmailSuggestion } from "./types/emailSuggestion";
import {
  getEmailSuggestions,
  markEmailProcessed,
  saveEmailFeedback,
} from "./services/api";



function App() {

  // Authentication
  const {
    token,
    email,
    password,
    message,
    loading,
    setEmail,
    setPassword,
    setMessage,
    handleLogin,
    handleLogout,
  } = useAuth();


  // Applications
  const {
    applications,
    save,
    remove,
    updateStatus,
    clearApplications,
  } = useApplications(token);


  // Application form
  const [showForm, setShowForm] = useState(false);

  const [company, setCompany] = useState("");
  const [position, setPosition] = useState("");
  const [location, setLocation] = useState("");
  const [status, setStatus] = useState("SAVED");

  const [editingApplication, setEditingApplication] = useState<JobApplication | null>(null);
  const [selectedApplication, setSelectedApplication] = useState<JobApplication | null>(null);
  const [applicationEvents, setApplicationEvents] = useState<ApplicationEvent[]>([]);
  const [applicationEventsLoading, setApplicationEventsLoading] = useState(false);


  // Search / filtering / sorting
  const [searchQuery, setSearchQuery] = useState("");
  const [statusFilter, setStatusFilter] = useState("ALL");
  const [sortOption, setSortOption] = useState("NEWEST");

  const [viewMode, setViewMode] = useState<"LIST" | "BOARD" | "EMAIL">("LIST");

  const [emailSuggestions, setEmailSuggestions] = useState<EmailSuggestion[]>([]);

  const [emailLoading, setEmailLoading] = useState(false);

  // Filter and sort applications
  const filteredApplications = applications
      .filter((application) => {

        const matchesSearch =
            application.company
                .toLowerCase()
                .includes(searchQuery.toLowerCase()) ||
            application.position
                .toLowerCase()
                .includes(searchQuery.toLowerCase());

        const matchesStatus =
            statusFilter === "ALL" ||
            application.status === statusFilter;

        return matchesSearch && matchesStatus;
      })
      .sort((a, b) => {

        if (sortOption === "COMPANY") {
          return a.company.localeCompare(b.company);
        }

        if (sortOption === "STATUS") {
          return a.status.localeCompare(b.status);
        }

        if (sortOption === "OLDEST") {
          return (a.dateApplied ?? "")
              .localeCompare(b.dateApplied ?? "");
        }

        return (b.dateApplied ?? "")
            .localeCompare(a.dateApplied ?? "");
      });


  // Dashboard statistics
  const {
    totalApplications,
    interviewCount,
    offerCount,
    interviewRate,
  } = getApplicationStats(applications);



  // Login form
  async function handleLoginSubmit(
      event: FormEvent<HTMLFormElement>
  ) {
    event.preventDefault();

    await handleLogin();
  }


  // Create or update application
  async function handleSaveApplication(
      event: FormEvent<HTMLFormElement>
  ) {
    event.preventDefault();

    const isEditing =
        editingApplication !== null;

    try {

      await save(
          {
            company,
            position,
            location,
            jobUrl:
                editingApplication?.jobUrl ?? null,
            status,
            dateApplied:
                status === "APPLIED"
                    ? editingApplication?.dateApplied ??
                    new Date()
                        .toISOString()
                        .slice(0, 10)
                    : editingApplication?.dateApplied ??
                    null,
            notes:
                editingApplication?.notes ?? null,
          },
          editingApplication?.id
      );



      resetApplicationForm();

    } catch {

      setMessage(
          isEditing
              ? "Could not update application."
              : "Could not create application."
      );
    }
  }


  // Delete application
  async function handleDeleteApplication(
      application: JobApplication
  ) {

    const confirmed = window.confirm(
        `Delete ${application.company} — ${application.position}?`
    );

    if (!confirmed) {
      return;
    }

    try {

      await remove(application);

    } catch {

      setMessage(
          "Could not delete application."
      );

      return;
    }
  }

  async function handleOpenEmailReview() {

    setViewMode("EMAIL");

    setEmailLoading(true);

    try {

      const suggestions =
          await getEmailSuggestions(token);

      setEmailSuggestions(
          suggestions
      );

    } catch {

      setMessage(
          "Could not load Gmail suggestions."
      );

    } finally {

      setEmailLoading(false);
    }
  }

  async function handleApproveEmailSuggestion(
      suggestion: EmailSuggestion
  ) {
    if (
        !suggestion.matchedApplicationId ||
        !suggestion.suggestedStatus
    ) {
      return;
    }

    const application =
        applications.find(
            (item) =>
                item.id ===
                suggestion.matchedApplicationId
        );

    if (!application) {
      return;
    }

    try {
      await updateStatus(
          application,
          suggestion.suggestedStatus
      );

      setEmailSuggestions((current) =>
          current.filter(
              (item) =>
                  item.id !== suggestion.id
          )
      );

    } catch {
      setMessage(
          "Could not apply Gmail suggestion."
      );
    }
  }

  async function handleEmailFeedback(
      suggestion: EmailSuggestion,
      actualLabel: string
  ) {
    try {

      await saveEmailFeedback(
          token,
          {
            gmailMessageId:
            suggestion.id,

            subject:
            suggestion.subject,

            sender:
            suggestion.from,

            body:
            suggestion.snippet,

            predictedLabel:
            suggestion.classification,

            actualLabel,

            confidence:
            suggestion.confidence,
          }
      );

      setMessage(
          "Thanks — feedback saved."
      );

    } catch {

      setMessage(
          "Could not save email feedback."
      );
    }
  }

    async function handleOpenApplicationDetails(
        application: JobApplication
    ) {
        setSelectedApplication(application);
        setApplicationEventsLoading(true);

        try {
            const events =
                await getApplicationEvents(
                    token,
                    application.id
                );

            setApplicationEvents(events);

        } catch {
            setMessage(
                "Could not load application history."
            );

            setApplicationEvents([]);
        } finally {
            setApplicationEventsLoading(false);
        }
    }

  async function handleCreateEmailSuggestion(
      suggestion: EmailSuggestion
  ) {
    if (
        !suggestion.company ||
        suggestion.classification === "OTHER"
    ) {
      return;
    }

    try {

      await save(
          {
            company: suggestion.company,

            position:
                suggestion.position ||
                "Position not identified",

            location: "",

            jobUrl: null,

            status:
            suggestion.classification,

            dateApplied:
                suggestion.classification === "APPLIED"
                    ? new Date()
                        .toISOString()
                        .slice(0, 10)
                    : null,

            notes:
                `Detected from Gmail: ${suggestion.subject}`,
          }
      );

      await markEmailProcessed(
          token,
          suggestion.id,
          "APPROVED"
      );

      setEmailSuggestions((current) =>
          current.filter(
              (item) =>
                  item.id !== suggestion.id
          )
      );

    } catch {

      setMessage(
          "Could not add detected application."
      );
    }
  }

    function handleCloseApplicationDetails() {
        setSelectedApplication(null);
        setApplicationEvents([]);
    }

  async function handleIgnoreEmailSuggestion(
      suggestion: EmailSuggestion
  ) {
    try {

      await markEmailProcessed(
          token,
          suggestion.id,
          "IGNORED"
      );

      await markEmailProcessed(
          token,
          suggestion.id,
          "APPROVED"
      );

      setEmailSuggestions((current) =>
          current.filter(
              (item) =>
                  item.id !== suggestion.id
          )
      );

    } catch {

      setMessage(
          "Could not ignore Gmail suggestion."
      );
    }
  }

  async function handleBoardStatusChange(
      application: JobApplication,
      newStatus: string
  ) {
    try {

      await updateStatus(
          application,
          newStatus
      );

    } catch {

      setMessage(
          "Could not update application status."
      );
    }
  }


  // Reset form
  function resetApplicationForm() {

    setEditingApplication(null);

    setShowForm(false);

    setCompany("");
    setPosition("");
    setLocation("");
    setStatus("SAVED");
  }


  // Logout
  function handleLogoutAndReset() {

    handleLogout();

    clearApplications();

    resetApplicationForm();

    setSearchQuery("");
    setStatusFilter("ALL");
    setSortOption("NEWEST");
  }


  // Login screen
  if (!token) {

    return (
        <LoginPage
            email={email}
            password={password}
            message={message}
            loading={loading}
            onEmailChange={setEmail}
            onPasswordChange={setPassword}
            onSubmit={handleLoginSubmit}
        />
    );
  }


  // Dashboard
  return (
      <DashboardPage

          applications={filteredApplications}

          searchQuery={searchQuery}
          statusFilter={statusFilter}
          sortOption={sortOption}

          onSearchChange={setSearchQuery}
          onStatusFilterChange={setStatusFilter}
          onSortChange={setSortOption}
          viewMode={viewMode}
          onViewModeChange={setViewMode}

          emailSuggestions={emailSuggestions}
          emailLoading={emailLoading}
          onOpenEmailReview={handleOpenEmailReview}
          onEmailFeedback={handleEmailFeedback}

          selectedApplication={selectedApplication}
          applicationEvents={applicationEvents}
          applicationEventsLoading={applicationEventsLoading}

          onOpenApplicationDetails={handleOpenApplicationDetails}
          onCloseApplicationDetails={handleCloseApplicationDetails}

          showForm={showForm}

          totalApplications={totalApplications}
          interviewCount={interviewCount}
          offerCount={offerCount}
          interviewRate={interviewRate}

          company={company}
          position={position}
          location={location}
          status={status}

          onShowForm={() => {
            resetApplicationForm();
            setShowForm(true);
          }}

          onLogout={handleLogoutAndReset}

          onCompanyChange={setCompany}
          onPositionChange={setPosition}
          onLocationChange={setLocation}
          onStatusChange={setStatus}

          onCreateApplication={
            handleSaveApplication
          }

          onDeleteApplication={
            handleDeleteApplication
          }

          onApproveEmailSuggestion={
            handleApproveEmailSuggestion
          }

          onCreateEmailSuggestion={
            handleCreateEmailSuggestion
          }

          onIgnoreEmailSuggestion={
            handleIgnoreEmailSuggestion
          }

          onBoardStatusChange={
            handleBoardStatusChange
          }

          editingApplication={
            editingApplication
          }

          onEditApplication={(application) => {

            setEditingApplication(application);

            setCompany(application.company);
            setPosition(application.position);

            setLocation(
                application.location ?? ""
            );

            setStatus(application.status);

            setShowForm(false);
          }}

          onCancelEdit={
            resetApplicationForm
          }
      />
  );
}


export default App;