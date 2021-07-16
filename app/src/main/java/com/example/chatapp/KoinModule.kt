import com.example.chatapp.model.repo.login.LoginRepository
import com.example.chatapp.viewmodel.LoginViewModel
import org.koin.androidx.compose.get
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel
import com.example.chatapp.model.repo.login.LoginRepositoryImpl
import com.example.chatapp.model.repo.verify_otp.VerifyOtpRepository
import com.example.chatapp.model.repo.verify_otp.VerifyOtpRepositoryImpl
import com.example.chatapp.viewmodel.VerifyOtpViewModel

val appModule = module {

    single<LoginRepository> { LoginRepositoryImpl(get()) }
    viewModel { LoginViewModel(get()) }

    single<VerifyOtpRepository> { VerifyOtpRepositoryImpl(get()) }
    viewModel { VerifyOtpViewModel(get()) }

}