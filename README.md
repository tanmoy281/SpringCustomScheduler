# SpringCustomScheduler
A custom cron scheduler using spring to change the cron expression dynamically


Please note that, scheduler will update the corn job execution time(if updated in db) only when it is the time to get the job executed. So, if suppose current job runs at 12am daily and we want to change it to run in every 1 minutes, then it will be reflected at the next execution time (that means tomorrow 12am) and not immediately when the time (cron expression) updated in the db.