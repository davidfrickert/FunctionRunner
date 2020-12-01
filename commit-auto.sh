cd "/home/david/Thesis/FaaS-GraalVM"
echo "Fetching changes"
git fetch
git pull
echo "Adding changes"
git add *
echo "Commiting"
git commit -m "Auto Commit ($(printf '%(%H:%M_%Y-%m-%d)T\n' -1))"
echo "Pushing"
git push
