import type { JobApplication } from "../types/application";

export function getApplicationStats(
    applications: JobApplication[]
) {
    const totalApplications = applications.length;

    const interviewCount = applications.filter(
        (application) =>
            application.status === "INTERVIEW"
    ).length;

    const offerCount = applications.filter(
        (application) =>
            application.status === "OFFER"
    ).length;

    const interviewRate =
        totalApplications === 0
            ? 0
            : Math.round(
                (interviewCount / totalApplications) * 100
            );

    return {
        totalApplications,
        interviewCount,
        offerCount,
        interviewRate,
    };
}