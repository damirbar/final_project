var CourseSchema = new mongoose.Schema({
    name: String,
    teacher: String,
    students: Array,
    fs: {},
    place: String,
    q&a: {},
    quiz: {},
    test: {},
    #ex: {},
    naz: Number,
    msg: {},
    register_date: Date,
    last_update: Date,
    notifications: Array
});
