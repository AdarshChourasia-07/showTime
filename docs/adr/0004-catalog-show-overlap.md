# ADR 0004: Reject overlapping shows on a screen in the catalog service

**Status:** accepted · **Date:** 2026-11-22

## Context
A partner could accidentally schedule two shows on the same screen at the same time, which would let us sell the same seat twice.

## Decision
When creating a show, load scheduled shows on that screen in a window around the new show and reject the request (409)
if any overlaps, including a 30-minute turnaround gap after each show.

## Consequences
- Simple and readable; covered by unit tests (`ShowTest`, `ShowServiceTest`).
- Two partners creating overlapping shows at the exact same moment could still race.
  Acceptable now (only the venue owner schedules a screen); a Postgres exclusion constraint on
  `tstzrange(starts_at, ends_at)` per screen would close the gap if needed later.
