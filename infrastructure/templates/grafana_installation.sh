!/bin/bash
sudo su
sudo yum update -y
echo -e "[grafana]\nname=grafana\nbaseurl=https://packages.grafana.com/oss/rpm\nrepo_gpgcheck=1\nenabled=1\ngpgcheck=1\ngpgkey=https://packages.grafana.com/gpg.key\nsslverify=1\nsslcacert=/etc/pki/tls/certs/ca-bundle.crt" >> /etc/yum.repos.d/grafana.repo
sudo yum install grafana -y
sudo systemctl daemon-reload
sudo systemctl start grafana-server
sudo systemctl enable grafana-server.service