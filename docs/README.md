# Alice User Guide

## Recurring deadlines and events

Deadlines and events can repeat daily, weekly, monthly, or yearly. Recurrence intervals are case-insensitive.
Todos cannot recur because they have no scheduled date.

Create a recurring task by adding `/r INTERVAL`:

```text
deadline Pay rent /by 2026-10-01 /r MONTHLY
event Team meeting /from 2026-10-08 1400 /to 2026-10-08 1500 /r weekly
```

You can also configure an incomplete deadline or event already in the list:

```text
repeat 3 daily
repeat 3 none
```

`repeat TASK_NUMBER none` stops future recurrence. Completed tasks and todos cannot be changed with `repeat`.

When you mark a recurring task complete, Alice keeps that completed occurrence and appends the first future
occurrence of the same task. The new occurrence is calculated from the current date and time, so missed intervals
are skipped. For events, both the start and end date-times move by the interval, preserving the event duration.

Recurring tasks are shown with a suffix such as `(repeats: monthly)`. Completed recurring tasks are immutable:
they cannot be unmarked or reconfigured, but `delete TASK_NUMBER` can remove any individual occurrence.

Saved recurring deadlines and events include an optional recurrence field. Existing saved tasks without that field
remain valid and load as non-recurring tasks.
