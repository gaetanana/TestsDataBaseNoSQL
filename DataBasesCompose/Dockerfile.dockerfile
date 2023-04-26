FROM ubuntu:20.04

RUN apt-get update && \
    apt-get install -y libdb-dev libdb++-dev

CMD ["bash"]
