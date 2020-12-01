cd "/home/david/Thesis/FaaS-GraalVM"
git fetch
git pull
git add *
git commit -m "Auto Commit ($(printf '%(%H:%M_%Y-%m-%d)T\n' -1))"
git push
