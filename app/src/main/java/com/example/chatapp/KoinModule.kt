import com.example.chatapp.model.repo.chat_image.ChatImageUploadRepository
import com.example.chatapp.model.repo.chat_image.ChatImageUploadRepositoryImpl
import com.example.chatapp.model.repo.login.LoginRepository
import org.koin.androidx.compose.get
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel
import com.example.chatapp.model.repo.login.LoginRepositoryImpl
import com.example.chatapp.model.repo.sync_contacts.SyncContactsRepository
import com.example.chatapp.model.repo.sync_contacts.SyncContactsRepositoryImpl
import com.example.chatapp.model.repo.verify_otp.VerifyOtpRepository
import com.example.chatapp.model.repo.verify_otp.VerifyOtpRepositoryImpl
import com.example.chatapp.viewmodel.*

val appModule = module {

    single<LoginRepository> { LoginRepositoryImpl(get()) }
    viewModel { LoginViewModel(get()) }

    single<VerifyOtpRepository> { VerifyOtpRepositoryImpl(get()) }
    viewModel { VerifyOtpViewModel(get()) }

    single<SyncContactsRepository> { SyncContactsRepositoryImpl(get()) }
    viewModel { SyncContactsViewModel(get()) }

    viewModel { FriendChatViewModel(get()) }

    viewModel { ChatUserViewModel(get()) }

    single<ChatImageUploadRepository> { ChatImageUploadRepositoryImpl(get()) }
    viewModel { UploadChatImageViewModel(get()) }

}