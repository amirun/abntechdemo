db.createUser(
    {
        user: "adminuser",
        pwd: "pAssw0rd",
        roles:[
            {
                role: "dbOwner",
                db:   "demodb"
            }
        ]
    }
);