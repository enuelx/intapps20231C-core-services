#!/bin/bash

mkdir ~/core-services
sudo su
apt-get update && apt-get upgrade -y
sudo apt install openjdk-17-jdk openjdk-17-jre -y